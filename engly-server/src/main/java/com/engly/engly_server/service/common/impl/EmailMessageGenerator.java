package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.GenerateTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class EmailMessageGenerator {

    public String generate(Map<String, Object> parameters, Resource resource) {
        String message;
        try {
            message = resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("[NotificationServiceImpl:generateMessage]Error reading message template", e);
            throw new GenerateTokenException("Cannot generate token: ", e);
        }
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String place = entry.getKey();
            Object value = entry.getValue();
            message = message.replace(place, value.toString());
        }
        return message;
    }
}
