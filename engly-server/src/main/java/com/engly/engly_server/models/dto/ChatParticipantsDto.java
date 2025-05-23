package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.Roles;

import java.time.Instant;

public record ChatParticipantsDto(String id,
                                  RoomsDto room,
                                  UsersDto user,
                                  Instant joinedAt,
                                  Instant leaveAt,
                                  Roles role) {
}
