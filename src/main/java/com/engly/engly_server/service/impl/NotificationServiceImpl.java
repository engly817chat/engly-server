package com.engly.engly_server.service.impl;


import com.engly.engly_server.models.entity.VerifyToken;
import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.service.EmailService;
import com.engly.engly_server.service.NotificationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
// This class is responsible for sending email notifications to users
public class NotificationServiceImpl implements NotificationService {

    private final VerifyTokenRepo tokenRepo;

    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);


    public NotificationServiceImpl(VerifyTokenRepo tokenRepo, EmailService emailService, EmailMessageGenerator messageGenerator) {
        this.tokenRepo = tokenRepo;
        this.emailService = emailService;
        this.messageGenerator = messageGenerator;
    }


    public void sendNotifyMessage(String email) {
        try {
            String token = RandomStringUtils.random(32, true, true);
            tokenRepo.save(new VerifyToken(token, email));

            String message = messageGenerator.generate(token, email);

            emailService.sendEmail(email, message);

            log.info("[NotificationServiceImpl:sendNotifyMessage]Notification was sent for email:{} with token:{}", email, token);
        } catch (Exception e) {
            log.error("[NotificationServiceImpl:sendNotifyMessage]Errors in user:{}", e.getMessage());
            throw new RuntimeException("token not saved exception email %s".formatted(email));
        }
    }


    @Override
    public void checkToken(String token, String email) {
        Optional<VerifyToken> verifyToken = tokenRepo.findByTokenAndEmail(token, email);

        if (verifyToken.isPresent()) {
            tokenRepo.delete(verifyToken.get());
            log.info("[NotificationServiceImpl:checkToken]Token:{} for email:{} was checked and deleted", token, email);
        } else {
            log.info("[NotificationServiceImpl:checkToken]Token:{} for email:{} was not verified", token, email);
            throw new RuntimeException("Token not found exception email %s".formatted(email));
        }
    }
}
