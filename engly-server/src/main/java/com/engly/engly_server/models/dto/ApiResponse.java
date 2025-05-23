package com.engly.engly_server.models.dto;

import java.time.Instant;

public record ApiResponse(String message, boolean success, Instant timestamp) { }
