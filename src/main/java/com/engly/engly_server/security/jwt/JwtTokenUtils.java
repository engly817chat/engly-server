package com.engly.engly_server.security.jwt;

import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import com.engly.engly_server.service.common.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Component
public class JwtTokenUtils {
    private final UserService userService;
    private final JwtDecoder jwtDecoder;

    public JwtTokenUtils(UserService userService, @Lazy JwtDecoder jwtDecoder) {
        this.userService = userService;
        this.jwtDecoder = jwtDecoder;
    }

    public String getUserName(Jwt jwtToken) {
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails) {
        if (getIfTokenIsExpired(jwtToken)) return false;
        return getUserName(jwtToken).equals(userDetails.getUsername());
    }

    private boolean getIfTokenIsExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    public UserDetails userDetails(String email) {
        return new UserDetailsImpl(userService.findUserEntityByEmail(email));
    }

    public Authentication validateToken(String jwt) {
        final Jwt token = jwtDecoder.decode(jwt);
        final var username = getUserName(token);
        final UserDetails userDetails = userDetails(username);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        final Object scopeObj = token.getClaim("scope");

        if (scopeObj instanceof String scopeStr)
            for (String s : scopeStr.split(" "))
                authorities.add(new SimpleGrantedAuthority("SCOPE_" + s));

        authorities.addAll(userDetails.getAuthorities());

        if (!isTokenValid(token, userDetails)) throw new TokenNotFoundException("Invalid JWT token");
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}
