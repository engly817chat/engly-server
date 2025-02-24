package com.engly.engly_server.service.impl;

import com.engly.engly_server.models.entity.VerifyToken;
import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTokenWorker {
    private final VerifyTokenRepo verifyTokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;

    private ScheduledTokenWorker(VerifyTokenRepo verifyTokenRepo, EmailService emailService, EmailMessageGenerator messageGenerator) {
        this.verifyTokenRepo = verifyTokenRepo;
        this.emailService = emailService;
        this.messageGenerator = messageGenerator;
    }

    @Scheduled(cron = "0 0 0 1/1 * ? *")
    private void deleteExpiredTokens() {
        verifyTokenRepo.deleteAllByDeleteDateLessThanEqual(LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 1/1 * ? *")
    private void notificateAfter15days() {
        LocalDateTime now = LocalDateTime.now();
        List<VerifyToken> toNotificate = verifyTokenRepo.findAllByDeleteDateBetween(now.plusDays(15), now.plusDays(16));
        toNotificate.forEach((verifyToken) ->
                emailService.sendEmail(verifyToken.getEmail(),
                        messageGenerator.generate(verifyToken.getToken(), verifyToken.getEmail())
                )
        );
    }
}
