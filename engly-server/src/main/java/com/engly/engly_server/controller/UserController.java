package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UsersDto> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<UsersDto>>> getUsers(
            @ParameterObject @PageableDefault(sort = "username,asc") Pageable pageable,
            PagedResourcesAssembler<UsersDto> assembler
    ) {
        final var users = userService.allUsers();
        return ResponseEntity.ok(assembler.toModel(new PageImpl<>(users, pageable, users.size())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.delete(id));
    }

    @DeleteMapping("/some")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<UsersDto>>> deleteSomeUsers(
            @ParameterObject @PageableDefault(sort = "username,asc", size = 25, page = 5) Pageable pageable,
            PagedResourcesAssembler<UsersDto> assembler,
            @RequestParam List<String> ids
    ) {
        final var users = userService.deleteSomeUsers(ids);
        return ResponseEntity.ok(assembler.toModel(new PageImpl<>(users, pageable, users.size())));
    }
}
