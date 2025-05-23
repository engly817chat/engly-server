package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.AdditionalInfoDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UserMapper.class)
public interface AdditionalInfoMapper {
    AdditionalInfoMapper INSTANCE = Mappers.getMapper(AdditionalInfoMapper.class);

    AdditionalInfoDto toDto(AdditionalInfo additionalInfo);
}
