package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.TokenType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthResponseDto(@JsonProperty("access_token") String accessToken,
                              @JsonProperty("access_token_expiry") int accessTokenExpiry,
                              @JsonProperty("token_type") TokenType tokenType,
                              @JsonProperty("user_name") String username) {
}
