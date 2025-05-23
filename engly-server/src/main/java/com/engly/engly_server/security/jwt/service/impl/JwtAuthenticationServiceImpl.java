package com.engly.engly_server.security.jwt.service.impl;

import com.engly.engly_server.models.dto.create.SignInDto;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.security.jwt.JwtHolder;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtHolder createAuthObject(Users users, HttpServletResponse response) {
        final var authentication = jwtTokenGenerator.createAuthenticationObject(users);
        final var accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        final var refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
        createRefreshToken(users, refreshToken, response);
        return new JwtHolder(refreshToken, accessToken);
    }

    @Override
    public JwtHolder authenticateData(Users user, Authentication authentication, HttpServletResponse response) {
        final var refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
        final var accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        createRefreshToken(user, refreshToken, response);
        return new JwtHolder(refreshToken, accessToken);
    }

    @Override
    public Authentication authenticate(SignInDto sign) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(sign.email(), sign.password()));
    }

    private void createRefreshToken(Users users, String refreshToken, HttpServletResponse httpServletResponse) {
        jwtTokenGenerator.createRefreshTokenCookie(httpServletResponse, refreshToken);
        refreshTokenRepo.save(RefreshToken.builder()
                .user(users)
                .token(refreshToken)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                .revoked(false)
                .build());
    }
}
