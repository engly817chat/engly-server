package com.engly.engly_server.models.enums;

import lombok.Getter;

@Getter
public enum Authority {
    READ,
    WRITE,
    DELETE,
    CREATE_GLOBAL,
    UPDATE_GLOBAL,
    DELETE_GLOBAL,
    ADMIN,
    AUTHORIZE,
    PASSWORD_RESET,
    ADDITIONAL_INFO,
    NOT_VERIFIED,
    LIMITED_ACCESS
}
