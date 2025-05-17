package com.engly.engly_server.controller;

import com.engly.engly_server.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refresh-token")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenRepository refreshTokenRepository;

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteAllTokens() {
        refreshTokenRepository.deleteAll();
    }
}
