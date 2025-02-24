package com.engly.engly_server.controller;

import com.engly.engly_server.service.impl.NotificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@Slf4j
@Tag(name = "Підтвердження email", description = "Контроллер для підтвердження email")
public class NotifyController {

    @Autowired
    private NotificationServiceImpl notificationService;

    @Operation(
            summary = "Надсилання посилання на email",
            description = """
                         1. Введіть email
                         2. Вкажіть URL: `http://localhost:8000/notify`\s
                         4. Выберите метод `POST` и нажмите `Send`
                        \s
                         Вам на пошту прийде лист з посиланням для підтвердження email перейдіть по ньому і виконається запит `http://localhost:8000/notify/check`
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посилання було надіслано на email"),
                    @ApiResponse(responseCode = "409", description = "Посилання не було надіслано або token не був згенерований коректно")
            }
    )
    @PostMapping
    public ResponseEntity<?> notifyUser(@RequestParam("email") String email) {
        try {
            notificationService.sendNotifyMessage(email);

            return ResponseEntity.ok().body("Message was sent succesfully");
        } catch (Exception e) {
            // Check the status of the notification service
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Message was not send try it again");
        }
    }

    @Operation(
            summary = "Підтвердження email за допомогою посилання",
            description = """
                         1. Перейдіть по згенерованому посиланню яке надійшло на email
                         Або
                         1. Виконайте запит `http://localhost:8000/notify/check?token={token}&email={email}`
                        \s
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Успішне підтвердження email"),
                    @ApiResponse(responseCode = "404", description = "Email не був підтверджений")
            }
    )
    @GetMapping("/check")
    public ResponseEntity<?> checkToken(@RequestParam("token") String token, @RequestParam("email") String email) {
        try {
            notificationService.checkToken(token, email);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("email verified successfully");
        } catch (Exception e) {
            // Check the status of the notification service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("token is invalid for this email address");
        }
    }
}
