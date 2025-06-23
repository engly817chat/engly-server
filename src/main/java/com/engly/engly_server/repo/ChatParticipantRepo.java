package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.ChatParticipants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepo extends JpaRepository<ChatParticipants, String> {
    Page<ChatParticipants> findAllByRoom_Id(String roomId, Pageable pageable);
}
