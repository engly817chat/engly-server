package com.engly.engly_server.security.cookiemanagement;

import com.engly.engly_server.exception.TokenNotFoundException;
import jakarta.servlet.http.Cookie;
import lombok.NonNull;

import java.util.Arrays;

public record CookieUtils(Cookie[] cookies) {
    public CookieUtils(Cookie[] cookies) {
        this.cookies = cookies == null ? null : cookies.clone();
    }

    public String getRefreshTokenCookie() {
        return cookies == null ? null : Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token is not found in cookies"));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final CookieUtils that = (CookieUtils) o;
        return Arrays.equals(cookies(), that.cookies());
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(cookies);
    }

    @Override
    public @NonNull String toString() {
        return "CookieUtils{" +
                "cookies=" + Arrays.toString(cookies) +
                '}';
    }
}