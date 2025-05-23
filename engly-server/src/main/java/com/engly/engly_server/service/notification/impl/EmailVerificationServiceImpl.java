package com.engly.engly_server.service.notification.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.service.common.EmailService;
import com.engly.engly_server.service.common.impl.EmailMessageGenerator;
import com.engly.engly_server.service.notification.EmailVerificationService;
import com.engly.engly_server.utils.emailsenderconfig.EmailSenderUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final VerifyTokenRepo tokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;
    private final UserRepo userRepo;
    private final SecurityService service;
    private final JwtAuthenticationService jwtAuthenticationService;

    @Value("classpath:/emailTemplates/verificationTemplate.txt")
    private Resource messageTemplate;
    @Value("${app.email.notification.check.url}")
    private String urlTemplate;
    @Value("#{'${sysadmin.email}'.split(',\\s*')}")
    private Set<String> sysadminEmails;


    @Override
    public EmailSendInfo sendMessage() {
        final var email = service.getCurrentUserEmail();
        try {
            return new EmailSenderUtil(
                    email, tokenRepo,
                    messageGenerator,
                    emailService,
                    messageTemplate,
                    urlTemplate,
                    "EmailVerificationServiceImpl:sendMessage").sendTokenEmail(userRepo::existsByEmail, TokenType.EMAIL_VERIFICATION);
        } catch (Exception e) {
            log.error("[EmailVerificationServiceImpl:sendNotifyMessage]Errors: {}", e.getMessage());
            throw new TokenNotFoundException("token not saved exception email " + email);
        }
    }


    @Override
    public AuthResponseDto checkToken(String token, HttpServletResponse response) {
        final var email = service.getCurrentUserEmail();
        return tokenRepo.findByTokenAndEmail(token, email).map(verifyToken -> {
            if (!verifyToken.getTokenType().equals(TokenType.EMAIL_VERIFICATION))
                throw new TokenNotFoundException("Invalid token for email verification");

            return userRepo.findByEmail(email).map(user -> {
                user.setEmailVerified(Boolean.TRUE);
                user.setRoles(sysadminEmails.contains(email) ? "ROLE_SYSADMIN" : "ROLE_USER");

                tokenRepo.delete(verifyToken);

                final var userSaved = userRepo.save(user);
                final var jwtHolder = jwtAuthenticationService.createAuthObject(userSaved, response);
                log.info("[NotificationServiceImpl:checkToken]Token:{} for email:{} was checked and deleted", token, email);

                return new AuthResponseDto(jwtHolder.accessToken(),
                        12,
                        TokenType.Bearer,
                        userSaved.getUsername());
            }).orElseThrow(() -> new NotFoundException("Invalid User"));
        }).orElseThrow(() -> new TokenNotFoundException("Token not found or already verified"));
    }
}
