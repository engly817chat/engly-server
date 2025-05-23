package com.engly.engly_server.service.notification.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.models.dto.update.PasswordResetRequest;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.service.common.EmailService;
import com.engly.engly_server.service.common.impl.EmailMessageGenerator;
import com.engly.engly_server.service.notification.PasswordResetService;
import com.engly.engly_server.utils.emailsenderconfig.EmailSenderUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {
    private final VerifyTokenRepo tokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationService jwtAuthenticationService;


    @Value("classpath:/emailTemplates/password-resetTemplate.txt")
    private Resource messageTemplate;
    @Value("${app.email.notification.password-reset.url}")
    private String urlTemplate;
    @Value("#{'${sysadmin.email}'.split(',\\s*')}")
    private Set<String> sysadminEmails;


    @Override
    public EmailSendInfo sendMessage(String email) {
        try {
            return new EmailSenderUtil(
                    email, tokenRepo,
                    messageGenerator,
                    emailService,
                    messageTemplate,
                    urlTemplate,
                    "PasswordResetServiceImpl:sendMessage").sendTokenEmail(userRepo::existsByEmail, TokenType.PASSWORD_RESET);
        } catch (Exception e) {
            log.error("[PasswordResetServiceImpl:sendMessage]Errors in user:{}", e.getMessage());
            throw new TokenNotFoundException("token not saved exception email %s".formatted(email));
        }
    }


    @Override
    public AuthResponseDto passwordReset(PasswordResetRequest data, HttpServletResponse response) {
        return tokenRepo.findById(data.token()).map(verifyToken -> {
            if (!verifyToken.getTokenType().equals(TokenType.PASSWORD_RESET))
                throw new TokenNotFoundException("Invalid token for password reset");

            return userRepo.findByEmail(verifyToken.getEmail()).map(user -> {
                user.setPassword(passwordEncoder.encode(data.newPassword()));
                if (Boolean.FALSE.equals(user.getEmailVerified())) {
                    user.setEmailVerified(true);
                    user.setRoles(sysadminEmails.contains(verifyToken.getEmail()) ? "ROLE_SYSADMIN" : "ROLE_USER");
                }

                tokenRepo.delete(verifyToken);

                final var jwtHolder = jwtAuthenticationService.createAuthObject(user, response);
                log.info("[NotificationServiceImpl:checkToken]Token:{} for email:{} was checked and deleted",
                        verifyToken.getToken(), verifyToken.getEmail());

                return new AuthResponseDto(jwtHolder.accessToken(),
                        12,
                        TokenType.Bearer,
                        user.getUsername());
            }).orElseThrow(() -> new NotFoundException("User not found"));
        }).orElseThrow(() -> new TokenNotFoundException("Token not found or already verified"));
    }
}


