package com.engly.engly_server.utils.fieldvalidation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldUtil {

    public static boolean isValid(Integer val) {
        return val != null && val > 0;
    }

    public static boolean isValid(LocalDate val) {
        return val != null;
    }

    public static boolean isValid(Object o) {
        return o != null;
    }

    public static void isValid(String email, String name, String providerId) {
        if (!isValid(email)|| !isValid(name) || !isValid(providerId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OAuth2 response");
    }
}
