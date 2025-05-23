package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.ActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepo extends JpaRepository<ActivityLogs, String> {
}
