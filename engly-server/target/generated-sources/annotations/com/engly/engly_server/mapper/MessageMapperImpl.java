package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.Message;
import java.time.Instant;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:41+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class MessageMapperImpl implements MessageMapper {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public MessagesDto toMessageDto(Message message) {
        if ( message == null ) {
            return null;
        }

        String id = null;
        UsersDto user = null;
        String content = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        Boolean isEdited = null;
        Boolean isDeleted = null;

        id = message.getId();
        user = userMapper.toUsersDto( message.getUser() );
        content = message.getContent();
        createdAt = message.getCreatedAt();
        updatedAt = message.getUpdatedAt();
        isEdited = message.getIsEdited();
        isDeleted = message.getIsDeleted();

        RoomsDto room = null;

        MessagesDto messagesDto = new MessagesDto( id, room, user, content, createdAt, updatedAt, isEdited, isDeleted );

        return messagesDto;
    }
}
