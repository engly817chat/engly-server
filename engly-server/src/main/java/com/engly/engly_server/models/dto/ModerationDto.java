package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.Action;

import java.time.Instant;

public record ModerationDto(String id,
                            RoomsDto room,
                            UsersDto moder,
                            UsersDto user,
                            Action action,
                            String reason,
                            Instant createdAt) {
}
