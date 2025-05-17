package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.ChatParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipants, String> {
}
