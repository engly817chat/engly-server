package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.models.dto.create.AdditionalRequestForGoogleUserDto;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import com.engly.engly_server.service.common.AdditionalService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdditionalServiceImpl implements AdditionalService {
    private final UserRepo userRepo;
    private final SecurityService securityService;
    private final JwtAuthenticationService jwtAuthenticationService;

    @Value("#{'${sysadmin.email}'.split(',\\s*')}")
    private Set<String> sysadminEmails;

    @Override
    public AuthResponseDto additionalRegistration(AdditionalRequestForGoogleUserDto additionalRequestForGoogleUserDto,
                                                  HttpServletResponse httpServletResponse) {
        final var email = securityService.getCurrentUserEmail();
        return userRepo.findByEmail(email)
                .map(user -> {
                    user.setRoles(sysadminEmails.contains(user.getEmail()) ? "ROLE_SYSADMIN" : "ROLE_USER");

                    final var additionalInfo = AdditionalInfo.builder()
                            .user(user)
                            .goal(additionalRequestForGoogleUserDto.goals())
                            .nativeLanguage(additionalRequestForGoogleUserDto.nativeLanguage())
                            .englishLevel(additionalRequestForGoogleUserDto.englishLevel())
                            .build();

                    user.setAdditionalInfo(additionalInfo);
                    final var savedUser = userRepo.save(user);

                    final Authentication newAuth = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), null,
                            new UserDetailsImpl(savedUser).getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(newAuth);

                    final var jwtHolder = jwtAuthenticationService.authenticateData(savedUser, newAuth, httpServletResponse);

                    return new AuthResponseDto(jwtHolder.accessToken(), 300, TokenType.Bearer, savedUser.getUsername());
                })
                .orElseThrow(() -> new UsernameNotFoundException("Invalid User"));
    }
}
