package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.ChatParticipantsDto;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.enums.Roles;
import java.time.Instant;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:41+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class ChatParticipantMapperImpl implements ChatParticipantMapper {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public ChatParticipantsDto toDtoForRooms(ChatParticipants chatParticipants) {
        if ( chatParticipants == null ) {
            return null;
        }

        String id = null;
        UsersDto user = null;
        Instant joinedAt = null;
        Instant leaveAt = null;
        Roles role = null;

        id = chatParticipants.getId();
        user = userMapper.toUsersDto( chatParticipants.getUser() );
        joinedAt = chatParticipants.getJoinedAt();
        leaveAt = chatParticipants.getLeaveAt();
        role = chatParticipants.getRole();

        RoomsDto room = null;

        ChatParticipantsDto chatParticipantsDto = new ChatParticipantsDto( id, room, user, joinedAt, leaveAt, role );

        return chatParticipantsDto;
    }
}
