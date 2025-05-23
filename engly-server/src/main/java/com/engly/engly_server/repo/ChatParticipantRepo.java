package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.ChatParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepo extends JpaRepository<ChatParticipants, String> {
}
