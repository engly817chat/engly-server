package com.engly.engly_server.cache;

import com.engly.engly_server.repo.ChatParticipantRepo;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatParticipantCache {
    private final ChatParticipantRepo chatParticipantRepo;

    @Cacheable(value = CacheName.PARTICIPANT_EXISTS,
            key = "#roomId + '-' + #userId",
            condition = "#userId != null && #roomId != null")
    public boolean isParticipantExists(String roomId, String userId) {
        return chatParticipantRepo.existsByRoomIdAndUserId(roomId, userId);
    }
}
