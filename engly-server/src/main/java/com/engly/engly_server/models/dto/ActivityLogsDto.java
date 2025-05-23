package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.Action;

import java.time.Instant;

public record ActivityLogsDto(String id,
                              UsersDto user,
                              Action action,
                              Instant createdAt) {
}
