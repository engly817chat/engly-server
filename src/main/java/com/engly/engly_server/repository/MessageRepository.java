package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findAllByRoomId(String roomId);

    @Query(value = "SELECT m FROM Message m WHERE m.room.id = :roomId AND m.content LIKE '%' || :keyString || '%'  ")
    List<Message> findAllMessagesByRoomIdContainingKeyString(String roomId, String keyString);
}
