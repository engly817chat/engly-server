package com.engly.engly_server.security.jwt;

import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
public class JwtTokenUtils {
    private final UserRepo userRepo;

    public JwtTokenUtils(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public String getUserName(Jwt jwtToken) {
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails){
        if (getIfTokenIsExpired(jwtToken)) return false;
        return getUserName(jwtToken).equals(userDetails.getUsername());
    }

    private boolean getIfTokenIsExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    public UserDetails userDetails(String email) {
        return userRepo.findByEmail(email)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Your account can be banned or deleted from the chat"));
    }
}
