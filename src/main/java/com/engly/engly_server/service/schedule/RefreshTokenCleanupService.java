package com.engly.engly_server.service.schedule;

import com.engly.engly_server.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void cleanupExpiredAndRevokedTokens() {
        refreshTokenRepository.findAllByExpiresAtBeforeOrRevokedIsTrue(Instant.now()).forEach(refreshToken -> {
            refreshTokenRepository.delete(refreshToken);
            log.info("Deleted refresh token {}", refreshToken);
        });
    }
}
