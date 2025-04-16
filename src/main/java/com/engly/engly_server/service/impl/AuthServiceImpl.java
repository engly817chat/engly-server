package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.enums.*;
import com.engly.engly_server.models.request.create.SignUpRequest;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.service.AuthService;
import com.engly.engly_server.utils.registrationchooser.RegistrationChooser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService, AuthenticationSuccessHandler {
    private final UserRepo userRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final Map<Provider, RegistrationChooser> chooserMap;

    public AuthServiceImpl(UserRepo userRepo, JwtTokenGenerator jwtTokenGenerator,
                           RefreshTokenRepo refreshTokenRepo,
                           List<RegistrationChooser> choosers) {
        this.userRepo = userRepo;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.refreshTokenRepo = refreshTokenRepo;
        this.chooserMap = choosers.stream()
                .collect(Collectors.toMap(RegistrationChooser::getProvider, o -> o));
    }

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            return userRepo.findByEmail(authentication.getName())
                    .map(users -> {
                        users.setLastLogin(Instant.now());
                        userRepo.save(users);
                        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
                        String refreshToken = refreshTokenRepo.save(jwtTokenGenerator
                                .createRefreshToken(users, authentication)).getRefreshToken();

                        jwtTokenGenerator.creatRefreshTokenCookie(response, refreshToken);
                        log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", users.getUsername());
                        return AuthResponseDto.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .accessTokenExpiry(15 * 60)
                                .username(users.getUsername())
                                .tokenType(TokenType.Bearer)
                                .build();
                    })
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new NotFoundException("USER NOT FOUND");
                    });
        } catch (Exception e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :{}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }

    @Override
    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(TokenType.Bearer.name()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token format");
        final String refreshToken = authorizationHeader.substring(7);

        var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
                .filter(tokens -> !tokens.isRevoked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));
        var users = refreshTokenEntity.getUser();

        var authentication = jwtTokenGenerator.createAuthenticationObject(users);
        final String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .username(users.getUsername())
                .tokenType(TokenType.Bearer)
                .refreshToken(refreshTokenRepo.save(jwtTokenGenerator
                        .createRefreshToken(users, authentication)).getRefreshToken())
                .build();
    }

    @Override
    public AuthResponseDto registerUser(SignUpRequest signUpRequest, HttpServletResponse httpServletResponse) {
        try {
            var registration = chooserMap.get(Provider.LOCAL).registration(signUpRequest);
            var authentication = jwtTokenGenerator.createAuthenticationObject(registration.getLeft());

            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = refreshTokenRepo.save(jwtTokenGenerator
                    .createRefreshToken(registration.getLeft(), authentication)).getRefreshToken();

            log.info("[AuthService:registerUser] User:{} Successfully registered", signUpRequest.username());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5 * 60)
                    .refreshToken(refreshToken)
                    .username(signUpRequest.username())
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
        final var oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");

        if (email == null || name == null || providerId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OAuth2 response");

        final var user = userRepo.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setLastLogin(Instant.now());
                    return existingUser;
                })
                .orElseGet(() -> chooserMap.get(Provider.GOOGLE)
                        .registration(new SignUpRequest(name, email, "Password123@",
                                EnglishLevels.A1, NativeLanguage.ENGLISH, Goals.DEFAULT, providerId))
                        .getLeft());

        final var userAuth = jwtTokenGenerator.createAuthenticationObject(user);
        final var accessToken = jwtTokenGenerator.generateAccessToken(userAuth);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getWriter(),
                AuthResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshTokenRepo.save(jwtTokenGenerator
                                .createRefreshToken(user, authentication)).getRefreshToken())
                        .accessTokenExpiry(15 * 60)
                        .username(user.getUsername())
                        .tokenType(TokenType.Bearer)
                        .build());
    }

    @Override
    public Map<String, Boolean> checkUsernameAvailability(String username) {
        Boolean isAvailable = !userRepo.existsByUsername(username);
        return Map.of("available", isAvailable);
    }

    @Override
    public Map<String, Boolean> checkEmailAvailability(String email) {
        Boolean isAvailable = !userRepo.existsByEmail(email);
        return Map.of("available", isAvailable);
    }
}
