package com.engly.engly_server.utils.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheName {
    public static final String USER_ID = "users";
    public static final String ALL_USER = "allUsers";

    public static final String ROOM_ID = "roomById";
    public static final String ROOM_CATEGORY = "roomsByCategory";
}
