package com.engly.engly_server.models.dto;

import java.time.Instant;

public record NotificationsDto(String id,
                               UsersDto user,
                               String content,
                               Boolean isRead,
                               Instant createdAt) {
}
