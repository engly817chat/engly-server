package com.engly.engly_server.models.dto;

import java.time.Instant;

public record MessagesDto(String id,
                          RoomsDto room,
                          UsersDto user,
                          String content,
                          Instant createdAt,
                          Instant updatedAt,
                          Boolean isEdited,
                          Boolean isDeleted) {
}
