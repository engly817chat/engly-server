package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.UsersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    void delete(String id);

    UsersDto findById(String id);

    List<UsersDto> allUsers();
}
