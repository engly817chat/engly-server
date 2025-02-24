package com.engly.engly_server.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode
@AllArgsConstructor
public class VerifyToken {
    @Id
    @Column(name = "token")
    private String token;

    @Column(name = "email")
    private String email;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;


    public VerifyToken(String token, String email) {
        this.token = token;
        this.email = email;
        expirationDate = LocalDateTime.now().plusDays(1);
        deleteDate = LocalDateTime.now().plusDays(30);
    }

    public VerifyToken() {
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }
}
