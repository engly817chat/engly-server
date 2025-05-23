package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.update.ProfileUpdateRequest;

public interface ProfileService {
    UsersDto getProfile();

    UsersDto updateProfile(ProfileUpdateRequest profileUpdateData);
}
