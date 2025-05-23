package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.NotificationsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.Notifications;
import java.time.Instant;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:40+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class NotificationMapperImpl implements NotificationMapper {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public NotificationsDto toNotificationsDto(Notifications notification) {
        if ( notification == null ) {
            return null;
        }

        String id = null;
        UsersDto user = null;
        String content = null;
        Boolean isRead = null;
        Instant createdAt = null;

        id = notification.getId();
        user = userMapper.toUsersDto( notification.getUser() );
        content = notification.getContent();
        isRead = notification.getIsRead();
        createdAt = notification.getCreatedAt();

        NotificationsDto notificationsDto = new NotificationsDto( id, user, content, isRead, createdAt );

        return notificationsDto;
    }
}
