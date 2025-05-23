package com.engly.engly_server.utils.fieldvalidation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldUtil {
    public static boolean isValid(String val) {
        return StringUtils.isNotBlank(val);
    }

    public static boolean isValid(Integer val) {
        return val != null && val > 0;
    }

    public static boolean isValid(LocalDate val) {
        return val != null;
    }

    public static boolean isValid(Boolean isActive) {
        return isActive != null;
    }

    public static <E extends Enum<E>> boolean isValid(E value) {
        return value != null;
    }

    public static boolean isValid(Long duration) {
        return duration != null;
    }

    public static boolean isValid(Object o) {
        return o != null;
    }
}
