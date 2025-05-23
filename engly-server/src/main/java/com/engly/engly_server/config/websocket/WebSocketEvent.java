package com.engly.engly_server.config.websocket;

import com.engly.engly_server.models.enums.EventType;

public record WebSocketEvent<T>(EventType type, T payload) { }
