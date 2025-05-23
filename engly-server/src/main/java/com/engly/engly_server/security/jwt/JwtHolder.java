package com.engly.engly_server.security.jwt;

public record JwtHolder(String refreshToken, String accessToken) { }
