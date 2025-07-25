package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserService {

    String NOT_FOUND_BY_EMAIL = "User not found with email: %s";
    String NOT_FOUND_BY_ID = "User not found with id: %s";

    ApiResponse delete(String id);

    UsersDto findById(String id);

    Optional<Users> findByEmail(String email);

    UsersDto findByEmailDto(String email);

    Page<UsersDto> allUsers(Pageable pageable);

    String getUsernameByEmail(String email);

    Integer deleteSomeUsers(List<String> ids);

    Users findUserEntityByEmail(String email);

    String getUserIdByEmail(String email);

    List<Users> findAllByRolesAndCreatedAtBefore(String roles, Instant expireBefore);

    void deleteAll(List<Users> users);
}
