package com.engly.engly_server.utils.registrationchooser;

import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.utils.passwordgenerateutil.PasswordGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public final class GoogleRegistration implements RegistrationChooser {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${dev.email}")
    private String devEmail;

    @Override
    public Pair<Users, AdditionalInfo> registration(SignUpRequestDto signUpRequestDto) {
        log.info("Registering Google user with email: {}", signUpRequestDto.email());
        userRepo.findByEmail(signUpRequestDto.email()).ifPresent(users -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this Google email already exists");
        });

        var user = Users.builder()
                .roles(signUpRequestDto.email().equals(devEmail)
                        ? "ROLE_ADMIN" : "ROLE_GOOGLE")
                .email(signUpRequestDto.email())
                .emailVerified(Boolean.TRUE)
                .username(signUpRequestDto.username())
                .password(passwordEncoder.encode(
                        PasswordGeneratorUtil.generatePassword(signUpRequestDto.email(), signUpRequestDto.username()))
                )
                .provider(Provider.GOOGLE)
                .lastLogin(Instant.now())
                .providerId(signUpRequestDto.providerId())
                .build();

        return Pair.of(userRepo.save(user), null);
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }
}
