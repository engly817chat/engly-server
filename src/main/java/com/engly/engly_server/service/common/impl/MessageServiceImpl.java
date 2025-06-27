package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.MessageMapper;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.create.ChatParticipantsRequestDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.dto.create.MessageRequestDto;
import com.engly.engly_server.models.enums.Roles;
import com.engly.engly_server.repo.MessageRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.service.common.MessageService;
import com.engly.engly_server.service.common.RoomService;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final RoomService roomService;
    private final UserService userService;
    private final SecurityService service;
    private final ChatParticipantsService chatParticipantsService;

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = CacheName.MESSAGE_ID, key = "#result.id()")
            },
            evict = {
                    @CacheEvict(value = CacheName.MESSAGES_BY_ROOM, key = "#messageRequestDto.roomId()"),
                    @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, key = "#messageRequestDto.roomId()"),
                    @CacheEvict(value = CacheName.ROOM_ID, key = "#messageRequestDto.roomId()")
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
            put = { @CachePut(value = CacheName.MESSAGE_ID, key = "#id") },
            evict = { @CacheEvict(value = CacheName.MESSAGES_BY_ROOM, key = "#result.room().id()") }
    )
    public MessagesDto editMessage(String id, String content) {
        return messageRepo.findById(id)
                .map(message -> {
                    message.setContent(content);
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
        return messageRepo.findAllByRoomId(roomId, pageable).map(MessageMapper.INSTANCE::toMessageDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessagesDto> findAllMessagesContainingKeyString(String roomId, String keyString, Pageable pageable) {
        return messageRepo.findAllMessagesByRoomIdContainingKeyString(roomId, keyString, pageable)
                .map(MessageMapper.INSTANCE::toMessageDto);
    }
    @Override
    @Transactional(readOnly = true)
    public List<UsersDto> findUsersWhoReadMessage(String messageId) {
        final var byId = messageRepo.findById(messageId);
        if (byId.isPresent()) {
            final var message = byId.get();

            return message.getRoom().getChatParticipants()
                    .stream()
                    .map(ChatParticipants::getUser)
                    .filter(user -> user.getLastLogin().isAfter(message.getCreatedAt()))
                    .map(UserMapper.INSTANCE::toUsersDto).toList();
        } else {
            throw new NotFoundException("message not found %s".formatted(messageId));
        }
    }
}
