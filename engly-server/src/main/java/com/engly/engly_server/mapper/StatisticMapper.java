package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.StatisticsDto;
import com.engly.engly_server.models.entity.Statistics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class, RoomMapper.class, CategoryMapper.class})
public interface StatisticMapper {
    StatisticMapper INSTANCE = Mappers.getMapper(StatisticMapper.class);

    @Mapping(target = "room", ignore = true)
    StatisticsDto toDto(Statistics statistics);
}
