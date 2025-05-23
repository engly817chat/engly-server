package com.engly.engly_server.service.schedule;

import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncompleteGoogleUserService {
    private final UserRepo userRepo;

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void deleteUsers() {
        final Instant expireBefore = Instant.now().minus(Duration.ofMinutes(15));
        final List<Users> incomplete = userRepo.findAllByRolesAndCreatedAtBefore("ROLE_GOOGLE", expireBefore);
        userRepo.deleteAll(incomplete);
    }
}
