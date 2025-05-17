package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.create.SignInDto;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import com.engly.engly_server.service.common.AuthService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "Автентифікація та Авторизація", description = "Контролер для реєстрації, входу та оновлення токену")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "User Authentication",
            description = """
                         Use Basic Auth in Postman:
                         1. Go to the Authorization tab and select `Basic Auth`
                         2. Enter username and password
                         3. Specify URL: `http://localhost:8000/sign-in`
                         4. Select `POST` method and click `Send`
                         5. Response: access token, refresh token and details
                         6. Add access token to Bearer
                        \s
                         Use Basic Auth in Swagger:
                         1. Go to the icon lock and use `Basic Auth`
                         2. Enter username and password -> authorize
                         3. Response: access token, refresh token and details
                         4. Try it out -> execute
                         4. Add access token to Bearer auth in swagger
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successful authentication. Returns access and refresh tokens."),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/sign-in")
    @RateLimiter(name = "AuthController")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody SignInDto signInDto, HttpServletResponse response) {
        return new ResponseEntity<>(authService.getJwtTokensAfterAuthentication(signInDto, response), HttpStatus.OK);
    }

    @Operation(
            summary = "User Authentication",
            description = """
                         Use refresh-token in Postman:
                         1. Go to the Authorization tab and select `Bearer`
                         2. Specify URL: `http://localhost:8000/refresh-token` POST
                         3. Click execute
                         4. Response: access token, refresh token and details
                        \s
                         Use refresh-token in Swagger:
                         1. Go to the icon lock and select `Bearer` -> put refresh token in there:
                         2. Write in param refresh token too
                         3. Click execute
                         4. Response: access token, refresh token and details
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Новий Access-токен успішно отримано"),
                    @ApiResponse(responseCode = "403", description = "Refresh-токен недійсний або закінчився термін дії")
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    @RateLimiter(name = "AuthController")
    public ResponseEntity<Object> getAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
            HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(refreshTokenFromCookie, httpServletResponse));
    }

    @Operation(
            summary = "Реєстрація нового користувача",
            description = """
                             For Goals:
                             DEFAULT("Default"),
                             IMPROVE_ENGLISH("Improve English"),
                             LEARN_NEW_LANGUAGE("Learn new language"),
                             MEET_NEW_PEOPLE("Meet new people");
                            \s
                         \s
                         Після введення всіх полів -> отримання access token + refresh token.
                         Так як це Email реєстрація буде видана роль NOT_VERIFIED. Це означає що ви не зможете зробити взагалі запит
                         окрім '/api/notify'. Якщо реєстрація через гугл підтверджувати не треба.
                    \s""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Дані користувача для реєстрації",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Користувача успішно зареєстровано"),
                    @ApiResponse(responseCode = "400", description = "Помилка валідації вхідних даних")
            }
    )
    @PostMapping("/sign-up")
    @RateLimiter(name = "AuthController")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto,
                                               BindingResult bindingResult, HttpServletResponse httpServletResponse) {

        log.info("[AuthController:registerUser]Signup Process Started for user:{}", signUpRequestDto.username());
        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerUser]Errors in user:{}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return new ResponseEntity<>(authService.registerUser(signUpRequestDto, httpServletResponse), HttpStatus.CREATED);
    }
}
