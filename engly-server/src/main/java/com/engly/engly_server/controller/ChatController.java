package com.engly.engly_server.controller;

import com.engly.engly_server.models.enums.EventType;
import com.engly.engly_server.models.dto.create.MessageRequestDto;
import com.engly.engly_server.models.dto.update.EditMessageRequest;
import com.engly.engly_server.service.common.MessageService;
import com.engly.engly_server.config.websocket.WebSocketEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_MESSAGES = "/topic/messages/";

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequestDto messageRequestDto) {
        final var message = messageService.sendMessage(messageRequestDto);
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + messageRequestDto.roomId(),
                new WebSocketEvent<>(EventType.MESSAGE_SEND, message));
    }

    @MessageMapping("/chat.editMessage")
    public void editMessage(@Payload EditMessageRequest request) {
        final var message = messageService.editMessage(request.id(), request.content());
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + message.id(),
                new WebSocketEvent<>(EventType.MESSAGE_EDIT, message));
    }

    @MessageMapping("/chat.deleteMessage")
    public void deleteMessage(@Payload String id) {
        final var message = messageService.deleteMessage(id);
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + message.room().id(),
                new WebSocketEvent<>(EventType.MESSAGE_DELETE, message));
    }
}
