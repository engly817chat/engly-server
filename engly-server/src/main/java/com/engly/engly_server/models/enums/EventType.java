package com.engly.engly_server.models.enums;

public enum EventType {
    MESSAGE_SEND,
    MESSAGE_EDIT,
    MESSAGE_DELETE,

    ROOM_CREATE,
    ROOM_UPDATE,
    ROOM_DELETE,

    USER_ONLINE,
    USER_OFFLINE,
    USER_TYPING
}
