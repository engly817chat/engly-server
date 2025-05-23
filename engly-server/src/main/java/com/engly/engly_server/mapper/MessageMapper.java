package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class, CategoryMapper.class})
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(target = "room", ignore = true)
    MessagesDto toMessageDto(Message message);
}
