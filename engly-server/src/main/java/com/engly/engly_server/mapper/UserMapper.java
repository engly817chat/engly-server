package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "activityLogs", ignore = true)
    @Mapping(target = "moderations", ignore = true)
    @Mapping(target = "userSettings", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "messages", ignore = true)
    UsersDto toUsersDto(Users user);
}
