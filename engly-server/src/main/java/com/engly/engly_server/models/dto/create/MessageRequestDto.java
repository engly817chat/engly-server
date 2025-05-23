package com.engly.engly_server.models.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequestDto(@NotBlank(message = "Room is empty") String roomId,
                                @Size(max = 200) @NotBlank(message = "Please input content") String content) {
}
