package com.engly.engly_server.models.dto;

import java.time.LocalDateTime;

public record StatisticsDto(String id,
                            RoomsDto room,
                            Long messageCount,
                            LocalDateTime lastMessageTime) {
}
