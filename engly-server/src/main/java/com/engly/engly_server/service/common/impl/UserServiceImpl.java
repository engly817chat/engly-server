package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.USER_ID, key = "#id"),
            @CacheEvict(value = CacheName.ALL_USER, allEntries = true)
    })
    public ApiResponse delete(String id) {
        return userRepo.findById(id)
                .map(users -> {
                    userRepo.delete(users);
                    return new ApiResponse("User deleted successfully", true, Instant.now());
                })
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USER_ID, key = "#id", sync = true)
    public UsersDto findById(String id) {
        return UserMapper.INSTANCE.toUsersDto(userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.ALL_USER, sync = true)
    public List<UsersDto> allUsers() {
        return userRepo.findAll().stream()
                .map(UserMapper.INSTANCE::toUsersDto)
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.USER_ID, allEntries = true),
            @CacheEvict(value = CacheName.ALL_USER, allEntries = true)
    })
    public List<UsersDto> deleteSomeUsers(List<String> ids) {
        return ids.stream()
                .map(id -> userRepo.findById(id)
                        .map(user -> {
                            userRepo.delete(user);
                            return UserMapper.INSTANCE.toUsersDto(user);
                        })
                        .orElse(null))
                .toList();
    }
}
