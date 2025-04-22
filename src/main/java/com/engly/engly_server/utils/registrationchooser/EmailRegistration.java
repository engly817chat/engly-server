package com.engly.engly_server.utils.registrationchooser;

import com.engly.engly_server.exception.FieldValidationException;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import com.engly.engly_server.repo.UserRepo;
import jakarta.validation.ValidationException;
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
public final class EmailRegistration implements RegistrationChooser {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${dev.email}")
    private String devEmail;

    @Override
    public Pair<Users, AdditionalInfo> registration(SignUpRequestDto signUpRequestDto) {
        try {
            log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpRequestDto);
            userRepo.findByEmail(signUpRequestDto.email()).ifPresent(users -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exist");
            });

            final var users = Users.builder()
                    .roles(signUpRequestDto.email().equals(devEmail) ? "ROLE_ADMIN" : "ROLE_NOT_VERIFIED")
                    .email(signUpRequestDto.email())
                    .emailVerified(Boolean.FALSE)
                    .username(signUpRequestDto.username())
                    .password(passwordEncoder.encode(signUpRequestDto.password()))
                    .provider(Provider.LOCAL)
                    .additionalInfo(AdditionalInfo.builder()
                            .goal(signUpRequestDto.goals())
                            .englishLevel(signUpRequestDto.englishLevel())
                            .nativeLanguage(signUpRequestDto.nativeLanguage())
                            .build())
                    .lastLogin(Instant.now())
                    .build();

            var save = userRepo.save(users);

            return Pair.of(save, save.getAdditionalInfo());
        } catch (ValidationException e) {
            log.error("[AuthService:registerUser]User Registration Failed: {}", e.getMessage());
            throw new FieldValidationException(e.getMessage());
        }
    }

    @Override
    public Provider getProvider() {
        return Provider.LOCAL;
    }
}
