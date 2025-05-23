package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.ActivityLogsDto;
import com.engly.engly_server.models.dto.AdditionalInfoDto;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.ModerationDto;
import com.engly.engly_server.models.dto.NotificationsDto;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.UserSettingsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Provider;
import java.time.Instant;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:40+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UsersDto toUsersDto(Users user) {
        if ( user == null ) {
            return null;
        }

        String id = null;
        String username = null;
        String email = null;
        String providerId = null;
        Instant createdAt = null;
        String roles = null;
        Boolean emailVerified = null;
        Instant updatedAt = null;
        Instant lastLogin = null;
        Provider provider = null;
        AdditionalInfoDto additionalInfo = null;

        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        providerId = user.getProviderId();
        createdAt = user.getCreatedAt();
        roles = user.getRoles();
        emailVerified = user.getEmailVerified();
        updatedAt = user.getUpdatedAt();
        lastLogin = user.getLastLogin();
        provider = user.getProvider();
        additionalInfo = additionalInfoToAdditionalInfoDto( user.getAdditionalInfo() );

        List<RoomsDto> rooms = null;
        List<ActivityLogsDto> activityLogs = null;
        List<ModerationDto> moderations = null;
        UserSettingsDto userSettings = null;
        List<NotificationsDto> notifications = null;
        List<MessagesDto> messages = null;

        UsersDto usersDto = new UsersDto( id, username, email, providerId, createdAt, roles, emailVerified, updatedAt, lastLogin, provider, additionalInfo, rooms, activityLogs, moderations, userSettings, notifications, messages );

        return usersDto;
    }

    protected AdditionalInfoDto additionalInfoToAdditionalInfoDto(AdditionalInfo additionalInfo) {
        if ( additionalInfo == null ) {
            return null;
        }

        String id = null;
        EnglishLevels englishLevel = null;
        NativeLanguage nativeLanguage = null;
        Goals goal = null;

        id = additionalInfo.getId();
        englishLevel = additionalInfo.getEnglishLevel();
        nativeLanguage = additionalInfo.getNativeLanguage();
        goal = additionalInfo.getGoal();

        AdditionalInfoDto additionalInfoDto = new AdditionalInfoDto( id, englishLevel, nativeLanguage, goal );

        return additionalInfoDto;
    }
}
