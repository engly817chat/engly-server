package com.engly.engly_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenerateTokenException extends RuntimeException {
    public GenerateTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
