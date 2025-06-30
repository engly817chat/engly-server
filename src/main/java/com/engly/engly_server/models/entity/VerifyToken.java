package com.engly.engly_server.models.entity;

import com.engly.engly_server.models.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode
@AllArgsConstructor
public class VerifyToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "token")
    private String token;

    @Column(name = "email")
    private String email;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    public VerifyToken(String token, String email, TokenType tokenType) {
        this.token = token;
        this.email = email;
        this.tokenType = tokenType;
        expirationDate = LocalDateTime.now().plusDays(1);
        deleteDate = LocalDateTime.now().plusDays(30);
    }

    public VerifyToken() {}
}
