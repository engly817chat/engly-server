package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepo extends JpaRepository<UserSettings, String> {
}
