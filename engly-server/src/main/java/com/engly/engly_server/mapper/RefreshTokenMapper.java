package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.RefreshTokenDto;
import com.engly.engly_server.models.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RefreshTokenMapper {
    RefreshTokenMapper INSTANCE = Mappers.getMapper(RefreshTokenMapper.class);

    @Mapping(target = "token", qualifiedByName = "normalizeToken")
    RefreshTokenDto toDisplayDto(RefreshToken source);

    @Named("normalizeToken")
    default String normalizeToken(String token) {
        if (token == null || token.length() <= 12) return token;
        return token.substring(0, 8) + "..." +
                token.substring(token.length() - 4);
    }
}
