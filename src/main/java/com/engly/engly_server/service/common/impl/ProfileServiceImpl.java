package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.update.ProfileUpdateRequest;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.ProfileService;
import com.engly.engly_server.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    @Cacheable(value = "userProfiles", key = "#root.target.getCurrentUserEmail()", unless = "#result == null")
    @Transactional(readOnly = true)
    public UsersDto getProfile() {
        final var email = securityService.getCurrentUserEmail();
        return UserMapper.INSTANCE.toUsersDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found")));
    }

    @Override
    @CacheEvict(value = "userProfiles", key = "#root.target.getCurrentUserEmail()")
    public UsersDto updateProfile(ProfileUpdateRequest profileUpdateData) {
        final var email = securityService.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (isValid(profileUpdateData.username())) user.setUsername(profileUpdateData.username());
                    if (isValid(profileUpdateData.goal())) user.getAdditionalInfo().setGoal(profileUpdateData.goal());
                    if (isValid(profileUpdateData.englishLevel()))
                        user.getAdditionalInfo().setEnglishLevel(profileUpdateData.englishLevel());
                    if (isValid(profileUpdateData.nativeLanguage()))
                        user.getAdditionalInfo().setNativeLanguage(profileUpdateData.nativeLanguage());

                    return UserMapper.INSTANCE.toUsersDto(userRepository.save(user));
                })
                .orElseThrow(() -> new NotFoundException("User Not Found"));
    }

    public String getCurrentUserEmail() {
        return securityService.getCurrentUserEmail();
    }
}
