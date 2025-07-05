package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    ApiResponse delete(String id);

    UsersDto findById(String id);

    Page<UsersDto> allUsers(Pageable pageable);

    Integer deleteSomeUsers(List<String> ids);

    Users findUserEntityByEmail(String email);
}
