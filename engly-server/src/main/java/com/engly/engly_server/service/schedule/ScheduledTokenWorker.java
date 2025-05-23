package com.engly.engly_server.service.schedule;

import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.service.common.EmailService;
import com.engly.engly_server.service.common.impl.EmailMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ScheduledTokenWorker {
    private final VerifyTokenRepo verifyTokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;
    @Value("classpath:emailTemplates/verificationTemplate.txt")
    private Resource messageTemplate;
    @Value("${app.email.notification.check.url}")
    private String urlTemplate;

    @Scheduled(cron = "0 0 0 * * ?")
    private void deleteExpiredTokens() {
        verifyTokenRepo.deleteAllByDeleteDateLessThanEqual(LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    private void notificateAfter15days() {
        final var now = LocalDateTime.now();
        final var toNotification = verifyTokenRepo.findAllByDeleteDateBetween(now.plusDays(15), now.plusDays(16));
        toNotification.forEach(verifyToken ->
                emailService.sendEmail(verifyToken.getEmail(),
                        messageGenerator.generate(
                                Map.of("[email]", verifyToken.getEmail(), "[link]", urlTemplate.formatted(verifyToken.getToken())),
                                messageTemplate)
                )
        );
    }
}
