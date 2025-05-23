package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.models.dto.update.PasswordResetRequest;
import com.engly.engly_server.service.notification.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
@Slf4j
@Tag(name = "Підтвердження email", description = "Контроллер для підтвердження email")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }


    @Operation(
            summary = "Надсилання посилання на email для відновлення паролю",
            description = """
                         Вам на пошту прийде лист з посиланням для відновлення паролю перейдіть по ньому і виконається запит `http://localhost:8000/api/password-reset`,
                         але повинен бути аксес токен у Bearer(отриманий при логіні для юзера без паролю)
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посилання було надіслано на email"),
                    @ApiResponse(responseCode = "409", description = "Посилання не було надіслано")
            }
    )
    @PostMapping("/send")
//    @RateLimiter(name = "PasswordResetController")
    public ResponseEntity<EmailSendInfo> notifyUserPasswordReset(@Valid
                                                                 @Email(message = "Isn't email")
                                                                 @NotBlank(message = "Email is blank")
                                                                 @Size(max = 50, message = "Email cannot exceed 50 characters. Please shorten your input.")
                                                                 @RequestParam
                                                                 String email) {
        try {
            return new ResponseEntity<>(passwordResetService.sendMessage(email), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Відновлення паролю за допомогою посилання",
            description = """
                         1. Перейдіть по згенерованому посиланню яке надійшло на email
                         На почту приходить повідомлення у вигляді: http://localhost:8000/api/password-reset/password-reset?token.
                         Копіюйте тільки password та вставляеєте його у параметр. Після чого отримуєте знову але оновлений access token та рефреш токен.
                         Як ввели аксес токен у Bearer, тепер можете робити запити.
                        \s
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успішне зміна паролю"),
                    @ApiResponse(responseCode = "404", description = "Пароль не був відновлений")
            }
    )
    @PostMapping
//    @RateLimiter(name = "PasswordResetController")
    public ResponseEntity<AuthResponseDto> passwordReset(@Valid @RequestBody PasswordResetRequest data, HttpServletResponse response) {
        return new ResponseEntity<>(passwordResetService.passwordReset(data, response), HttpStatus.OK);
    }
}
