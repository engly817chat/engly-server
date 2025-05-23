package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.AvailabilityResponseDto;
import com.engly.engly_server.models.dto.GoogleAvailabilityDto;

public interface UserValidationService {
    GoogleAvailabilityDto firstLogin();
    AvailabilityResponseDto isUsernameAvailable(String username);
    AvailabilityResponseDto isEmailAvailable(String email);
}
