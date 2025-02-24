package com.engly.engly_server.service;

public interface NotificationService {
    void sendNotifyMessage(String email);

    void checkToken(String token, String email);

}
