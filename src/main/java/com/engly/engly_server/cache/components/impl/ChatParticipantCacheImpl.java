package com.engly.engly_server.cache.components.impl;

import com.engly.engly_server.cache.components.ChatParticipantCache;
import com.engly.engly_server.repo.ChatParticipantRepo;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatParticipantCacheImpl implements ChatParticipantCache {
    private final ChatParticipantRepo chatParticipantRepo;

    @Override
    @Cacheable(value = CacheName.PARTICIPANT_EXISTS,
            key = "#roomId + '-' + #userId",
            condition = "#userId != null && #roomId != null")
    public boolean isParticipantExists(String roomId, String userId) {
        return chatParticipantRepo.existsByRoomIdAndUserId(roomId, userId);
    }
}
