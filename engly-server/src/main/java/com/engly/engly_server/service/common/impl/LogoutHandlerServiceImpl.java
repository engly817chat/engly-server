package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repo.RefreshTokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerServiceImpl implements LogoutHandler {
    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && !authHeader.startsWith(TokenType.Bearer.name())) return;

        final var refreshToken = Objects.requireNonNull(authHeader).substring(7);

        refreshTokenRepo.findByTokenAndRevokedIsFalse(refreshToken)
                .map(token -> {
                    token.setRevoked(true);
                    return refreshTokenRepo.save(token);
                }).orElseThrow(() -> new NotFoundException("Refresh token not found"));
    }
}