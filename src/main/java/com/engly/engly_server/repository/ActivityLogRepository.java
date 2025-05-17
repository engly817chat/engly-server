package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.ActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLogs, String> {
}
