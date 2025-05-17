package com.engly.engly_server.models.dto;

public record RateLimitStatus(int remaining, long windowReset, long banExpires) { }
