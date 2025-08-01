package com.engly.engly_server.security.jwt.service;

import com.engly.engly_server.models.dto.create.SignInRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.security.jwt.JwtHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface JwtAuthenticationService {
    JwtHolder createAuthObject(Users users, HttpServletResponse response);

    JwtHolder authenticateData(Users users, Authentication authentication, HttpServletResponse response);

    Authentication authenticateCredentials(SignInRequest sign);

    JwtHolder createAuthObjectForVerification(Users users, HttpServletResponse response);

    Authentication newAuthentication(Users users);
}
