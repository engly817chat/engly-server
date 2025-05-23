package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.entity.Categories;
import java.time.Instant;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:40+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoriesDto toCategoriesDto(Categories categories) {
        if ( categories == null ) {
            return null;
        }

        String id = null;
        String description = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = categories.getId();
        description = categories.getDescription();
        createdAt = categories.getCreatedAt();
        updatedAt = categories.getUpdatedAt();

        int activeRoomsCount = categories.getRooms() != null ? categories.getRooms().size() : 0;
        String icon = categories.getName().getIcon();
        String name = categories.getName().getVal();
        List<RoomsDto> rooms = null;

        CategoriesDto categoriesDto = new CategoriesDto( id, name, description, createdAt, updatedAt, activeRoomsCount, icon, rooms );

        return categoriesDto;
    }
}
