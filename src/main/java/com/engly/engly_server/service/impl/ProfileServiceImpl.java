package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.update.ProfileUpdateRequest;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.service.ProfileService;
import com.engly.engly_server.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.check;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepo userRepo;

    @Override
    public UsersDto getProfile() {
        final var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return UserMapper.INSTANCE.toUsersDto(userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found")));
    }

    @Override
    public UsersDto updateProfile(ProfileUpdateRequest profileUpdateData) {
        final var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .map(user -> {
                    if (check(profileUpdateData.username())) user.setUsername(profileUpdateData.username());
                    if (check(profileUpdateData.goal())) user.getAdditionalInfo().setGoal(profileUpdateData.goal());
                    if (check(profileUpdateData.englishLevel()))
                        user.getAdditionalInfo().setEnglishLevel(profileUpdateData.englishLevel());
                    if (check(profileUpdateData.nativeLanguage()))
                        user.getAdditionalInfo().setNativeLanguage(profileUpdateData.nativeLanguage());

                    return UserMapper.INSTANCE.toUsersDto(userRepo.save(user));
                })
                .orElseThrow(() -> new NotFoundException("User Not Found"));
    }
}
