package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.create.SignInDto;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponseDto getJwtTokensAfterAuthentication(SignInDto sign, HttpServletResponse response);

    AuthResponseDto getAccessTokenUsingRefreshToken(String refreshToken, HttpServletResponse response);

    AuthResponseDto registerUser(SignUpRequestDto signUpRequestDto, HttpServletResponse httpServletResponse);
}
