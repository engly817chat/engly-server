package com.engly.engly_server.security.jwt.service.impl;

import com.engly.engly_server.models.dto.create.SignInDto;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.security.jwt.JwtHolder;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 25;

    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final AuthenticationManager authenticationManager;

    private final TriFunction<Users, Authentication, HttpServletResponse, JwtHolder> tokenProcessor =
            (user, auth, response) -> {
                final String accessToken = jwtTokenGenerator.generateAccessToken(auth);
                final String refreshToken = jwtTokenGenerator.generateRefreshToken(auth);

                jwtTokenGenerator.createRefreshTokenCookie(response, refreshToken);
                refreshTokenRepo.save(RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .createdAt(Instant.now())
                        .expiresAt(Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS))
                        .revoked(false)
                        .build());

                return new JwtHolder(refreshToken, accessToken);
            };

    /**
     * Creates access and refresh tokens and sends refresh token in cookie.
     */
    @Override
    public JwtHolder createAuthObject(Users user, HttpServletResponse response) {
        final var authentication = jwtTokenGenerator.createAuthenticationObject(user);
        return tokenProcessor.apply(user, authentication, response);
    }

    /**
     * Creates and stores tokens given a valid authentication.
     */
    @Override
    public JwtHolder authenticateData(Users user, Authentication authentication, HttpServletResponse response) {
        return tokenProcessor.apply(user, authentication, response);
    }

    /**
     * Authenticates the user with email/password credentials.
     */
    @Override
    public Authentication authenticateCredentials(SignInDto sign) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(sign.email(), sign.password()));
    }

    /**
     * Used post-verification to create new tokens and set SecurityContext.
     */
    @Override
    public JwtHolder createAuthObjectForVerification(Users user, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        final Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), new UserDetailsImpl(user).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProcessor.apply(user, authentication, response);
    }
}
