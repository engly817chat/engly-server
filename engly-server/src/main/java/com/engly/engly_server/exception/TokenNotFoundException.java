package com.engly.engly_server.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String ex) {
        super(ex);
    }
}
