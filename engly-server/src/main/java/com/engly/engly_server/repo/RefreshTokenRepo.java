package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndRevokedIsFalse(String refreshToken);

    boolean existsByUserAndRevoked(Users user, boolean revoked);

    Optional<RefreshToken> findByUser(Users user);

    List<RefreshToken> findAllByExpiresAtBeforeOrRevokedIsTrue(Instant now);
}
