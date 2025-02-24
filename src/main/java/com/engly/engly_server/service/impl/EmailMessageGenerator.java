package com.engly.engly_server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailMessageGenerator {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    @Value("classpath:messageTemplate.txt")
    private Resource MESSAGE_TEMPLATE;
    @Value("${app.email.notification.check.url}")
    private String URL_TEMPLATE;


    public String generate(String token, String email) {
        String message;
        try {
            message = MESSAGE_TEMPLATE.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("[NotificationServiceImpl:generateMessage]Error reading message template", e);
            throw new RuntimeException(e.getMessage());
        }
        message = message.replace("[Ім'я користувача]", email);
        message = message.replace("[Посилання для підтвердження]", URL_TEMPLATE.formatted(email, token));
        return message;
    }
}
