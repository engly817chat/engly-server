package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Moderation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationRepo extends JpaRepository<Moderation, String> {
}
