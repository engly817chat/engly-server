package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.ActivityLogsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.ActivityLogs;
import com.engly.engly_server.models.enums.Action;
import java.time.Instant;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:40+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class ActivityLogMapperImpl implements ActivityLogMapper {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public ActivityLogsDto toDto(ActivityLogs activityLogs) {
        if ( activityLogs == null ) {
            return null;
        }

        String id = null;
        UsersDto user = null;
        Action action = null;
        Instant createdAt = null;

        id = activityLogs.getId();
        user = userMapper.toUsersDto( activityLogs.getUser() );
        action = activityLogs.getAction();
        createdAt = activityLogs.getCreatedAt();

        ActivityLogsDto activityLogsDto = new ActivityLogsDto( id, user, action, createdAt );

        return activityLogsDto;
    }
}
