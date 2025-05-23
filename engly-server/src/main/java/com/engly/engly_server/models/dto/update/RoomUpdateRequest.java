package com.engly.engly_server.models.dto.update;

import com.engly.engly_server.models.enums.CategoryType;
import jakarta.validation.constraints.Size;

public record RoomUpdateRequest(@Size(min = 2, max = 50) String name,
                                String description,
                                CategoryType newCategory,
                                String updateCreatorByEmail) {
}
