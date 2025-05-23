package com.engly.engly_server.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;

public record ApiErrorResponse(String message,
                               int code,
                               String details,
                               LocalDateTime timestamp) {

    public ApiErrorResponse responseConfiguration(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(code);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return this;
    }

    public void throwException(ServletOutputStream outputStream) throws IOException {
        final var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.writeValue(outputStream, this);
    }
}
