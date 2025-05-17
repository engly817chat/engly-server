package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.service.notification.EmailVerificationService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-verify")
@Slf4j
@Tag(name = "Підтвердження email", description = "Контроллер для підтвердження email")
public class EmailVerifyController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerifyController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @Operation(
            summary = "Надсилання посилання на email для підтвердження пошти",
            description = """
                         Вам на пошту прийде лист з посиланням для підтвердження email перейдіть по ньому і виконається запит `http://localhost:8000/api/email-verify/check?token=your-token`,
                         але повинен бути аксес токен у Bearer(отриманий при реєстрації)
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посилання було надіслано на email"),
                    @ApiResponse(responseCode = "409", description = "Посилання не було надіслано або token не був згенерований коректно")
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_NOT_VERIFIED')")
    @PostMapping
    @RateLimiter(name = "EmailVerifyController")
    public ResponseEntity<EmailSendInfo> notifyUserEmailVerify() {
        try {
            return new ResponseEntity<>(emailVerificationService.sendMessage(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Підтвердження email за допомогою посилання",
            description = """
                         1. Перейдіть по згенерованому посиланню яке надійшло на email
                         На почту приходить повідомлення у вигляді: http://localhost:8000/api/email-verify/check?token=your-token.
                         Копіюйте тільки your-token та вставляеєте його у параметр. Після чого отримуєте знову але оновлений access token та рефреш токен.
                         Як ввели аксес токен у Bearer, тепер можете робити запити.
                        \s
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Успішне підтвердження email"),
                    @ApiResponse(responseCode = "404", description = "Email не був підтверджений")
            }
    )
    @GetMapping("/check")
    @PreAuthorize("hasAuthority('SCOPE_NOT_VERIFIED')")
    public ResponseEntity<AuthResponseDto> checkToken(@RequestParam("token") String token, HttpServletResponse response) {
        return new ResponseEntity<>(emailVerificationService.checkToken(token, response), HttpStatus.OK);
    }
}
