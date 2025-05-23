package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.ModerationDto;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.Moderation;
import com.engly.engly_server.models.enums.Action;
import java.time.Instant;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:40+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class ModerationMapperImpl implements ModerationMapper {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public ModerationDto toDtoForRoom(Moderation moderation) {
        if ( moderation == null ) {
            return null;
        }

        String id = null;
        UsersDto moder = null;
        UsersDto user = null;
        Action action = null;
        String reason = null;
        Instant createdAt = null;

        id = moderation.getId();
        moder = userMapper.toUsersDto( moderation.getModer() );
        user = userMapper.toUsersDto( moderation.getUser() );
        action = moderation.getAction();
        reason = moderation.getReason();
        createdAt = moderation.getCreatedAt();

        RoomsDto room = null;

        ModerationDto moderationDto = new ModerationDto( id, room, moder, user, action, reason, createdAt );

        return moderationDto;
    }
}
