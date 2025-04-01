package com.engly.engly_server.models.dto;

import com.engly.engly_server.models.enums.CategoryType;

import java.time.Instant;
import java.util.List;

public record CategoriesDto(String id,
                            String name,
                            String description,
                            Instant createdAt,
                            Instant updatedAt,
                            int activeRoomsCount,
                            String icon,
                            List<RoomsDto> rooms) {
}
