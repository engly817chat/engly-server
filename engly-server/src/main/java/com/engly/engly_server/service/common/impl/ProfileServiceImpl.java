package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.update.ProfileUpdateRequest;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.ProfileService;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepo userRepo;
    private final SecurityService securityService;

    @Override
    @Cacheable(
            value = "userProfiles",
            key = "#root.target.getCurrentUserEmail()",
            sync = true
    )
    @Transactional(readOnly = true)
    public UsersDto getProfile() {
        final var email = securityService.getCurrentUserEmail();
        return UserMapper.INSTANCE.toUsersDto(userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found")));
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = "userProfiles", key = "#root.target.getCurrentUserEmail()")
            },
            evict = {
                    @CacheEvict(value = "users", allEntries = true),
                    @CacheEvict(value = "allUsers", allEntries = true),
                    @CacheEvict(value = CacheName.USER_ID, key = "#result.id")
            }
    )
    @Transactional
    public UsersDto updateProfile(ProfileUpdateRequest profileUpdateData) {
        final var email = securityService.getCurrentUserEmail();
        return userRepo.findByEmail(email)
                .map(user -> {
                    if (isValid(profileUpdateData.username())) user.setUsername(profileUpdateData.username());
                    if (isValid(profileUpdateData.goal())) user.getAdditionalInfo().setGoal(profileUpdateData.goal());
                    if (isValid(profileUpdateData.englishLevel()))
                        user.getAdditionalInfo().setEnglishLevel(profileUpdateData.englishLevel());
                    if (isValid(profileUpdateData.nativeLanguage()))
                        user.getAdditionalInfo().setNativeLanguage(profileUpdateData.nativeLanguage());

                    return UserMapper.INSTANCE.toUsersDto(userRepo.save(user));
                })
                .orElseThrow(() -> new NotFoundException("User Not Found"));
    }

    @SuppressWarnings("unused")
    public String getCurrentUserEmail() {
        return securityService.getCurrentUserEmail();
    }
}
