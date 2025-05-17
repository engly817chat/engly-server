package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notifications, String> {
}
