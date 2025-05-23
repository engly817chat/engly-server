package com.engly.engly_server.models.dto;

import java.time.Instant;

public record RefreshTokenDto(Long id,
                              String token,
                              boolean revoked,
                              UsersDto user,
                              Instant createdAt,
                              Instant expiresAt) {
}
