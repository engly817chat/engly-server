package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.ActivityLogsDto;
import com.engly.engly_server.models.dto.AdditionalInfoDto;
import com.engly.engly_server.models.dto.ChatParticipantsDto;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.ModerationDto;
import com.engly.engly_server.models.dto.NotificationsDto;
import com.engly.engly_server.models.dto.RefreshTokenDto;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.StatisticsDto;
import com.engly.engly_server.models.dto.UserSettingsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.ActivityLogs;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.entity.Moderation;
import com.engly.engly_server.models.entity.Notifications;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.entity.Statistics;
import com.engly.engly_server.models.entity.UserSettings;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Action;
import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.enums.Roles;
import com.engly.engly_server.models.enums.Theme;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:40+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class RefreshTokenMapperImpl implements RefreshTokenMapper {

    @Override
    public RefreshTokenDto toDisplayDto(RefreshToken source) {
        if ( source == null ) {
            return null;
        }

        String token = null;
        Long id = null;
        boolean revoked = false;
        UsersDto user = null;
        Instant createdAt = null;
        Instant expiresAt = null;

        token = normalizeToken( source.getToken() );
        id = source.getId();
        revoked = source.isRevoked();
        user = usersToUsersDto( source.getUser() );
        createdAt = source.getCreatedAt();
        expiresAt = source.getExpiresAt();

        RefreshTokenDto refreshTokenDto = new RefreshTokenDto( id, token, revoked, user, createdAt, expiresAt );

        return refreshTokenDto;
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

    protected MessagesDto messageToMessagesDto(Message message) {
        if ( message == null ) {
            return null;
        }

        String id = null;
        RoomsDto room = null;
        UsersDto user = null;
        String content = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        Boolean isEdited = null;
        Boolean isDeleted = null;

        id = message.getId();
        room = roomsToRoomsDto( message.getRoom() );
        user = usersToUsersDto( message.getUser() );
        content = message.getContent();
        createdAt = message.getCreatedAt();
        updatedAt = message.getUpdatedAt();
        isEdited = message.getIsEdited();
        isDeleted = message.getIsDeleted();

        MessagesDto messagesDto = new MessagesDto( id, room, user, content, createdAt, updatedAt, isEdited, isDeleted );

        return messagesDto;
    }

    protected List<MessagesDto> messageListToMessagesDtoList(List<Message> list) {
        if ( list == null ) {
            return null;
        }

        List<MessagesDto> list1 = new ArrayList<MessagesDto>( list.size() );
        for ( Message message : list ) {
            list1.add( messageToMessagesDto( message ) );
        }

        return list1;
    }

    protected ChatParticipantsDto chatParticipantsToChatParticipantsDto(ChatParticipants chatParticipants) {
        if ( chatParticipants == null ) {
            return null;
        }

        String id = null;
        RoomsDto room = null;
        UsersDto user = null;
        Instant joinedAt = null;
        Instant leaveAt = null;
        Roles role = null;

        id = chatParticipants.getId();
        room = roomsToRoomsDto( chatParticipants.getRoom() );
        user = usersToUsersDto( chatParticipants.getUser() );
        joinedAt = chatParticipants.getJoinedAt();
        leaveAt = chatParticipants.getLeaveAt();
        role = chatParticipants.getRole();

        ChatParticipantsDto chatParticipantsDto = new ChatParticipantsDto( id, room, user, joinedAt, leaveAt, role );

        return chatParticipantsDto;
    }

    protected List<ChatParticipantsDto> chatParticipantsListToChatParticipantsDtoList(List<ChatParticipants> list) {
        if ( list == null ) {
            return null;
        }

        List<ChatParticipantsDto> list1 = new ArrayList<ChatParticipantsDto>( list.size() );
        for ( ChatParticipants chatParticipants : list ) {
            list1.add( chatParticipantsToChatParticipantsDto( chatParticipants ) );
        }

        return list1;
    }

    protected ModerationDto moderationToModerationDto(Moderation moderation) {
        if ( moderation == null ) {
            return null;
        }

        String id = null;
        RoomsDto room = null;
        UsersDto moder = null;
        UsersDto user = null;
        Action action = null;
        String reason = null;
        Instant createdAt = null;

        id = moderation.getId();
        room = roomsToRoomsDto( moderation.getRoom() );
        moder = usersToUsersDto( moderation.getModer() );
        user = usersToUsersDto( moderation.getUser() );
        action = moderation.getAction();
        reason = moderation.getReason();
        createdAt = moderation.getCreatedAt();

        ModerationDto moderationDto = new ModerationDto( id, room, moder, user, action, reason, createdAt );

        return moderationDto;
    }

    protected List<ModerationDto> moderationListToModerationDtoList(List<Moderation> list) {
        if ( list == null ) {
            return null;
        }

        List<ModerationDto> list1 = new ArrayList<ModerationDto>( list.size() );
        for ( Moderation moderation : list ) {
            list1.add( moderationToModerationDto( moderation ) );
        }

        return list1;
    }

    protected StatisticsDto statisticsToStatisticsDto(Statistics statistics) {
        if ( statistics == null ) {
            return null;
        }

        String id = null;
        RoomsDto room = null;
        Long messageCount = null;
        LocalDateTime lastMessageTime = null;

        id = statistics.getId();
        room = roomsToRoomsDto( statistics.getRoom() );
        messageCount = statistics.getMessageCount();
        lastMessageTime = statistics.getLastMessageTime();

        StatisticsDto statisticsDto = new StatisticsDto( id, room, messageCount, lastMessageTime );

        return statisticsDto;
    }

    protected List<StatisticsDto> statisticsListToStatisticsDtoList(List<Statistics> list) {
        if ( list == null ) {
            return null;
        }

        List<StatisticsDto> list1 = new ArrayList<StatisticsDto>( list.size() );
        for ( Statistics statistics : list ) {
            list1.add( statisticsToStatisticsDto( statistics ) );
        }

        return list1;
    }

    protected RoomsDto roomsToRoomsDto(Rooms rooms) {
        if ( rooms == null ) {
            return null;
        }

        String id = null;
        String name = null;
        String description = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        UsersDto creator = null;
        List<MessagesDto> messages = null;
        List<ChatParticipantsDto> chatParticipants = null;
        List<ModerationDto> moderation = null;
        List<StatisticsDto> statistics = null;

        id = rooms.getId();
        name = rooms.getName();
        description = rooms.getDescription();
        createdAt = rooms.getCreatedAt();
        updatedAt = rooms.getUpdatedAt();
        creator = usersToUsersDto( rooms.getCreator() );
        messages = messageListToMessagesDtoList( rooms.getMessages() );
        chatParticipants = chatParticipantsListToChatParticipantsDtoList( rooms.getChatParticipants() );
        moderation = moderationListToModerationDtoList( rooms.getModeration() );
        statistics = statisticsListToStatisticsDtoList( rooms.getStatistics() );

        RoomsDto roomsDto = new RoomsDto( id, name, description, createdAt, updatedAt, creator, messages, chatParticipants, moderation, statistics );

        return roomsDto;
    }

    protected List<RoomsDto> roomsListToRoomsDtoList(List<Rooms> list) {
        if ( list == null ) {
            return null;
        }

        List<RoomsDto> list1 = new ArrayList<RoomsDto>( list.size() );
        for ( Rooms rooms : list ) {
            list1.add( roomsToRoomsDto( rooms ) );
        }

        return list1;
    }

    protected ActivityLogsDto activityLogsToActivityLogsDto(ActivityLogs activityLogs) {
        if ( activityLogs == null ) {
            return null;
        }

        String id = null;
        UsersDto user = null;
        Action action = null;
        Instant createdAt = null;

        id = activityLogs.getId();
        user = usersToUsersDto( activityLogs.getUser() );
        action = activityLogs.getAction();
        createdAt = activityLogs.getCreatedAt();

        ActivityLogsDto activityLogsDto = new ActivityLogsDto( id, user, action, createdAt );

        return activityLogsDto;
    }

    protected List<ActivityLogsDto> activityLogsListToActivityLogsDtoList(List<ActivityLogs> list) {
        if ( list == null ) {
            return null;
        }

        List<ActivityLogsDto> list1 = new ArrayList<ActivityLogsDto>( list.size() );
        for ( ActivityLogs activityLogs : list ) {
            list1.add( activityLogsToActivityLogsDto( activityLogs ) );
        }

        return list1;
    }

    protected UserSettingsDto userSettingsToUserSettingsDto(UserSettings userSettings) {
        if ( userSettings == null ) {
            return null;
        }

        String id = null;
        UsersDto user = null;
        Theme theme = null;
        boolean notifications = false;
        NativeLanguage interfaceLanguage = null;

        id = userSettings.getId();
        user = usersToUsersDto( userSettings.getUser() );
        theme = userSettings.getTheme();
        notifications = userSettings.isNotifications();
        interfaceLanguage = userSettings.getInterfaceLanguage();

        UserSettingsDto userSettingsDto = new UserSettingsDto( id, user, theme, notifications, interfaceLanguage );

        return userSettingsDto;
    }

    protected NotificationsDto notificationsToNotificationsDto(Notifications notifications) {
        if ( notifications == null ) {
            return null;
        }

        String id = null;
        UsersDto user = null;
        String content = null;
        Boolean isRead = null;
        Instant createdAt = null;

        id = notifications.getId();
        user = usersToUsersDto( notifications.getUser() );
        content = notifications.getContent();
        isRead = notifications.getIsRead();
        createdAt = notifications.getCreatedAt();

        NotificationsDto notificationsDto = new NotificationsDto( id, user, content, isRead, createdAt );

        return notificationsDto;
    }

    protected List<NotificationsDto> notificationsListToNotificationsDtoList(List<Notifications> list) {
        if ( list == null ) {
            return null;
        }

        List<NotificationsDto> list1 = new ArrayList<NotificationsDto>( list.size() );
        for ( Notifications notifications : list ) {
            list1.add( notificationsToNotificationsDto( notifications ) );
        }

        return list1;
    }

    protected UsersDto usersToUsersDto(Users users) {
        if ( users == null ) {
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
        List<RoomsDto> rooms = null;
        List<ActivityLogsDto> activityLogs = null;
        List<ModerationDto> moderations = null;
        UserSettingsDto userSettings = null;
        List<NotificationsDto> notifications = null;
        List<MessagesDto> messages = null;

        id = users.getId();
        username = users.getUsername();
        email = users.getEmail();
        providerId = users.getProviderId();
        createdAt = users.getCreatedAt();
        roles = users.getRoles();
        emailVerified = users.getEmailVerified();
        updatedAt = users.getUpdatedAt();
        lastLogin = users.getLastLogin();
        provider = users.getProvider();
        additionalInfo = additionalInfoToAdditionalInfoDto( users.getAdditionalInfo() );
        rooms = roomsListToRoomsDtoList( users.getRooms() );
        activityLogs = activityLogsListToActivityLogsDtoList( users.getActivityLogs() );
        moderations = moderationListToModerationDtoList( users.getModerations() );
        userSettings = userSettingsToUserSettingsDto( users.getUserSettings() );
        notifications = notificationsListToNotificationsDtoList( users.getNotifications() );
        messages = messageListToMessagesDtoList( users.getMessages() );

        UsersDto usersDto = new UsersDto( id, username, email, providerId, createdAt, roles, emailVerified, updatedAt, lastLogin, provider, additionalInfo, rooms, activityLogs, moderations, userSettings, notifications, messages );

        return usersDto;
    }
}
