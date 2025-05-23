package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.ModerationDto;
import com.engly.engly_server.models.entity.Moderation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class, RoomMapper.class, CategoryMapper.class})
public interface ModerationMapper {
    ModerationMapper INSTANCE = Mappers.getMapper(ModerationMapper.class);

    @Mapping(target = "room", ignore = true)
    ModerationDto toDtoForRoom(Moderation moderation);
}
