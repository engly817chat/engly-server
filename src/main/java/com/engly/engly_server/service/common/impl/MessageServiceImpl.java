package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.MessageMapper;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.create.ChatParticipantsRequestDto;
import com.engly.engly_server.models.dto.create.MessageRequestDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Roles;
import com.engly.engly_server.repo.MessageRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.*;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final RoomService roomService;
    private final UserService userService;
    private final SecurityService service;
    private final ChatParticipantsService chatParticipantsService;
    private final MessageReadService messageReadService;
    private final TaskExecutor taskExecutor;

    public MessageServiceImpl(MessageRepo messageRepo, RoomService roomService,
                              UserService userService, SecurityService service,
                              ChatParticipantsService chatParticipantsService, MessageReadService messageReadService,
                              @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        this.messageRepo = messageRepo;
        this.roomService = roomService;
        this.userService = userService;
        this.service = service;
        this.chatParticipantsService = chatParticipantsService;
        this.messageReadService = messageReadService;
        this.taskExecutor = taskExecutor;
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = CacheName.MESSAGE_ID, key = "#result.id()")
            },
            evict = {
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM, key = "#messageRequestDto.roomId()"),
                    @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, key = "#messageRequestDto.roomId()")
            }
    )
    public MessagesDto sendMessage(MessageRequestDto messageRequestDto) {
        final var name = service.getCurrentUserEmail();
        final var user = userService.findUserEntityByEmail(name);
        final var room = roomService.findRoomEntityById(messageRequestDto.roomId());
        final var savedMessage = messageRepo.save(Message.builder()
                .isEdited(Boolean.FALSE)
                .isDeleted(Boolean.FALSE)
                .content(messageRequestDto.content())
                .user(user)
                .room(room)
                .build());
        chatParticipantsService.addParticipant(new ChatParticipantsRequestDto(room, user, Roles.ROLE_USER));
        return MessageMapper.INSTANCE.toMessageDto(savedMessage);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.MESSAGE_ID, key = "#id"),
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM, key = "#result.room().id()")
            }
    )
    public MessagesDto deleteMessage(String id) {
        return messageRepo.findById(id).map(message -> {
                    messageRepo.delete(message);
                    return MessageMapper.INSTANCE.toMessageDto(message);
                })
                .orElseThrow(() -> new NotFoundException("Cannot found this message"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.MESSAGE_ID, key = "#id")
    public MessagesDto findById(String id) {
        return messageRepo.findById(id)
                .map(MessageMapper.INSTANCE::toMessageDto)
                .orElseThrow(() -> new NotFoundException("Message not found"));
    }

    @Override
    @Caching(
            put = {@CachePut(value = CacheName.MESSAGE_ID, key = "#id")},
            evict = {@CacheEvict(value = CacheName.MESSAGES_BY_ROOM, key = "#result.room().id()")}
    )
    public MessagesDto editMessage(String id, String content) {
        return messageRepo.findById(id)
                .map(message -> {
                    message.setContent(content);
                    message.setIsEdited(Boolean.TRUE);
                    return MessageMapper.INSTANCE.toMessageDto(messageRepo.save(message));
                })
                .orElseThrow(() -> new NotFoundException("Cannot found this message"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.MESSAGES_BY_ROOM,
            key = "#roomId",
            condition = "#pageable.pageNumber == 0",
            unless = "#result.isEmpty()"
    )
    public Page<MessagesDto> findAllMessageInCurrentRoom(String roomId, Pageable pageable) {
        final String currentUserEmail = service.getCurrentUserEmail();
        final Users currentUser = userService.findUserEntityByEmail(currentUserEmail);

        final Page<Message> messages = messageRepo.findAllByRoomId(roomId, pageable);

        var messageIds = messages.getContent().stream()
                .map(Message::getId)
                .toList();

        CompletableFuture.runAsync(() ->
                        messageReadService.markMessageAsRead(messageIds, currentUser.getId()), taskExecutor)
                .exceptionally(throwable -> {
                    log.error("Failed to mark messages as read: {}", throwable.getMessage());
                    return null;
                });

        return messages.map(MessageMapper.INSTANCE::toMessageDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessagesDto> findAllMessagesContainingKeyString(String roomId, String keyString, Pageable pageable) {
        return messageRepo.findAllMessagesByRoomIdContainingKeyString(roomId, keyString, pageable)
                .map(MessageMapper.INSTANCE::toMessageDto);
    }
}
