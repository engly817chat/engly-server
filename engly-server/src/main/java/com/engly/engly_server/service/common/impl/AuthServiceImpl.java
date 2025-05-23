package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.SignInException;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.create.SignInDto;
import com.engly.engly_server.models.enums.*;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.service.common.AuthService;
import com.engly.engly_server.security.registration.RegistrationChooser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService, AuthenticationSuccessHandler {
    private final UserRepo userRepo;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final RefreshTokenRepo refreshTokenRepo;
    private final Map<Provider, RegistrationChooser> chooserMap;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public AuthServiceImpl(UserRepo userRepo, JwtAuthenticationService jwtAuthenticationService,
                           RefreshTokenRepo refreshTokenRepo, List<RegistrationChooser> choosers) {
        this.userRepo = userRepo;
        this.jwtAuthenticationService = jwtAuthenticationService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.chooserMap = choosers.stream()
                .collect(Collectors.toMap(RegistrationChooser::getProvider, o -> o));
    }

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(SignInDto signInDto, HttpServletResponse response) {
        try {
            final var authentication = jwtAuthenticationService.authenticate(signInDto);
            return userRepo.findByEmail(signInDto.email())
                    .map(users -> {
                        users.setLastLogin(Instant.now());
                        final var savedUser = userRepo.save(users);
                        final var jwtHolder = jwtAuthenticationService.authenticateData(savedUser, authentication, response);
                        log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", users.getUsername());
                        return AuthResponseDto.builder()
                                .accessToken(jwtHolder.accessToken())
                                .accessTokenExpiry(15 * 60)
                                .username(users.getUsername())
                                .tokenType(TokenType.Bearer)
                                .build();
                    })
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", signInDto.email());
                        return new NotFoundException("USER NOT FOUND");
                    });
        } catch (BadCredentialsException e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :{}", e.getMessage());
            throw new SignInException(e.getMessage());
        }
    }


    @Override
    public AuthResponseDto getAccessTokenUsingRefreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token format");

        final var refreshTokenEntity = refreshTokenRepo.findByTokenAndRevokedIsFalse(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));
        refreshTokenEntity.setRevoked(true);
        final var users = refreshTokenRepo.save(refreshTokenEntity).getUser();

        final var jwtHolder = jwtAuthenticationService.createAuthObject(users, response);

        return AuthResponseDto.builder()
                .accessToken(jwtHolder.accessToken())
                .accessTokenExpiry(5 * 60)
                .username(users.getUsername())
                .tokenType(TokenType.Bearer)
                .build();
    }

    @Override
    public AuthResponseDto registerUser(SignUpRequestDto signUpRequestDto, HttpServletResponse httpServletResponse) {
        try {
            final var user = chooserMap.get(Provider.LOCAL).registration(signUpRequestDto);
            final var jwtHolder = jwtAuthenticationService.createAuthObject(user, httpServletResponse);

            log.info("[AuthService:registerUser] User:{} Successfully registered", signUpRequestDto.username());
            return AuthResponseDto.builder()
                    .accessToken(jwtHolder.accessToken())
                    .accessTokenExpiry(5 * 60)
                    .username(signUpRequestDto.username())
                    .tokenType(TokenType.Bearer)
                    .build();
        } catch (ValidationException e) {
            log.error("[AuthService:registerUser]Exception while registering the user due to :{}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        final var oauth2User = Optional.ofNullable(authentication.getPrincipal())
                .map(OAuth2User.class::cast)
                .orElseThrow(() -> new NotFoundException("Invalid OAuth2 response"));

        final String email = oauth2User.getAttribute("email");
        final String name = oauth2User.getAttribute("name");
        final String providerId = oauth2User.getAttribute("sub");

        if (email == null || name == null || providerId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OAuth2 response");

        final var user = userRepo.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setLastLogin(Instant.now());
                    return existingUser;
                })
                .orElseGet(() -> chooserMap.get(Provider.GOOGLE)
                        .registration(new SignUpRequestDto(name, email, "Password123@",
                                EnglishLevels.A1, NativeLanguage.ENGLISH, Goals.DEFAULT, providerId)));

        final var jwtHolder = jwtAuthenticationService.createAuthObject(user, response);

        final String redirectUrl = frontendUrl + "/google-auth/callback?" +
                "access_token=" + URLEncoder.encode(jwtHolder.accessToken(), StandardCharsets.UTF_8) +
                "&refresh_token=" + URLEncoder.encode(jwtHolder.refreshToken(), StandardCharsets.UTF_8) +
                "&expires_in=" + (15 * 60) +
                "&token_type=Bearer" +
                "&username=" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}
