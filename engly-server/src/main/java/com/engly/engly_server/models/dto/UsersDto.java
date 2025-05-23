package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.Provider;

import java.time.Instant;
import java.util.List;

public record UsersDto(String id,
                       String username,
                       String email,
                       String providerId,
                       Instant createdAt,
                       String roles,
                       Boolean emailVerified,
                       Instant updatedAt,
                       Instant lastLogin,
                       Provider provider,
                       AdditionalInfoDto additionalInfo,
                       List<RoomsDto> rooms,
                       List<ActivityLogsDto> activityLogs,
                       List<ModerationDto> moderations,
                       UserSettingsDto userSettings,
                       List<NotificationsDto> notifications,
                       List<MessagesDto> messages) {
}
