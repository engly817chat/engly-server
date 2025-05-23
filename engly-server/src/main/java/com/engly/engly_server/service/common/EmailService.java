package com.engly.engly_server.service.common;

@FunctionalInterface
public interface EmailService {
    void sendEmail(String to, String body);
}
