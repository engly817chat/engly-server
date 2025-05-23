package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notifications, String> {
}
