package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CategoryMapper.class, UserMapper.class, ChatParticipantMapper.class, ModerationMapper.class, StatisticMapper.class})
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "chatParticipants", source = "chatParticipants")
    @Mapping(target = "moderation", source = "moderation")
    @Mapping(target = "statistics", source = "statistics")
    RoomsDto roomToDto(Rooms rooms);
}
