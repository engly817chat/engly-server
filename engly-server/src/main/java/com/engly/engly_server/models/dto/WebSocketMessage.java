package com.engly.engly_server.models.dto;

public record WebSocketMessage(String roomId, String senderEmail, String content) {
}
