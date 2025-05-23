package com.engly.engly_server.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String ex) {
        super(ex);
    }
}
