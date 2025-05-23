package com.engly.engly_server.service.schedule;

import com.engly.engly_server.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenCleanupService {
    private final RefreshTokenRepo refreshTokenRepo;

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void cleanupExpiredAndRevokedTokens() {
        refreshTokenRepo.findAllByExpiresAtBeforeOrRevokedIsTrue(Instant.now()).forEach(refreshToken -> {
            refreshTokenRepo.delete(refreshToken);
            log.info("Deleted refresh token {}", refreshToken);
        });
    }
}
