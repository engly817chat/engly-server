package com.engly.engly_server.models.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoomRequestDto(
        @Valid
        @NotBlank(message = "Name of room is blank")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
        String name,

        @Valid
        @Size(max = 100, message = "Description must be less then 100 characters.")
        String description) {
}
