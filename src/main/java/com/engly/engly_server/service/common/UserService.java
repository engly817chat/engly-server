package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.ApiResponse;
import com.engly.engly_server.models.dto.response.UsersDto;
import com.engly.engly_server.models.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface UserService {

    String NOT_FOUND_BY_EMAIL = "User not found with email: %s";
    String NOT_FOUND_BY_ID = "User not found with id: %s";

    ApiResponse delete(String id);

    UsersDto findById(String id);

    Users findEntityById(String id);

    Page<UsersDto> allUsers(Pageable pageable);

    String getUsernameByEmail(String email);

    boolean existsById(String id);

    Integer deleteSomeUsers(List<String> ids);

    Users findUserEntityByEmail(String email);

    String getUserIdByEmail(String email);

    List<Users> findAllByRolesAndCreatedAtBefore(String roles, Instant expireBefore);

    void deleteAll(List<Users> users);
}
