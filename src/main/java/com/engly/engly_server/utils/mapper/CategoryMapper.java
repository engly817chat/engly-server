package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "activeRoomsCount", expression = "java(categories.getRooms() != null ? categories.getRooms().size() : 0)")
    CategoriesDto toCategoriesDto(Categories categories);
}
