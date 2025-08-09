package com.engly.engly_server.controller;

import com.engly.engly_server.models.events.MessageReadEvent;
import com.engly.engly_server.models.events.MessageReadersRequest;
import com.engly.engly_server.models.events.WebSocketEvent;
import com.engly.engly_server.models.events.TypingEvent;
import com.engly.engly_server.models.dto.request.*;
import com.engly.engly_server.models.enums.EventType;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.security.root.RequireRoomPermission;
import com.engly.engly_server.service.common.MessageReadService;
import com.engly.engly_server.service.common.MessageService;
import com.engly.engly_server.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SCOPE_WRITE')")
public class ChatController {
    private final MessageService messageService;
    private final MessageReadService messageReadService;
    private final UserService userService;
    private final SecurityService securityService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_MESSAGES = "/topic/messages/";

    @MessageMapping("/chat/message.send")
    public void sendMessage(@Payload CreateMessageData createMessageData) {
        final var message = messageService.sendMessage(createMessageData);
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + message.roomId(),
                new WebSocketEvent<>(EventType.MESSAGE_SEND, message));
    }

    @MessageMapping("/chat/message.markAsRead")
    public void markMessagesAsRead(@Payload MarkAsReadRequest request) {
        final var userId = userService.getUserIdByEmail(securityService.getCurrentUserEmail());
        messageReadService.markMessageAsRead(request.messageId(), userId);

        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + request.roomId(),
                new WebSocketEvent<>(EventType.MESSAGE_READ,
                        new MessageReadEvent(request.messageId(), userId, Instant.now()))
        );
    }

    @MessageMapping("/chat/message.readers")
    @RequireRoomPermission(permission = "ROOM_READ")
    public void getReaders(@Payload MessageReadersRequest messageReadersRequest) {
        final var pageable = messageReadersRequest.pageable() != null
                ? messageReadersRequest.pageable()
                : PageRequest.of(0, 8);

        final var message = messageReadService.getUsersWhoReadMessage(messageReadersRequest.messageId(), pageable);
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + messageReadersRequest.roomId(),
                new WebSocketEvent<>(EventType.MESSAGE_READERS, message));
    }

    @MessageMapping("/chat/message.edit")
    @RequireRoomPermission(permission = "ROOM_WRITE")
    public void editMessage(@Payload EditMessageRequest request) {
        final var message = messageService.editMessage(request.id(), request.content());
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + request.roomId(),
                new WebSocketEvent<>(EventType.MESSAGE_EDIT, message));
    }

    @MessageMapping("/chat/message.delete")
    @RequireRoomPermission(permission = "ROOM_WRITE")
    public void deleteMessage(@Payload DeleteMessageRequest deleteMessageRequest) {
        messageService.deleteMessage(deleteMessageRequest.messageId());
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + deleteMessageRequest.roomId(),
                new WebSocketEvent<>(EventType.MESSAGE_DELETE,
                        "Message with ID " + deleteMessageRequest.messageId() + " has been deleted"));
    }

    @MessageMapping("/chat/user.typing")
    public void userTyping(@Payload TypingRequest typingRequest) {
        final var username = userService.getUsernameByEmail(securityService.getCurrentUserEmail());
        final var typingEvent = new TypingEvent(
                typingRequest.roomId(),
                username,
                typingRequest.isTyping(),
                Instant.now()
        );

        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + typingRequest.roomId(),
                new WebSocketEvent<>(EventType.USER_TYPING, typingEvent)
        );
    }
}
