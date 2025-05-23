package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.VerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerifyTokenRepo extends JpaRepository<VerifyToken, String> {
    Optional<VerifyToken> findByTokenAndEmail(String token, String email);

    void deleteAllByDeleteDateLessThanEqual(LocalDateTime deleteDate);

    List<VerifyToken> findAllByDeleteDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
