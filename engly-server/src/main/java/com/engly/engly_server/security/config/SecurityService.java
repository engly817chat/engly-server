package com.engly.engly_server.security.config;

import com.engly.engly_server.models.enums.Authority;
import com.engly.engly_server.models.enums.Roles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SecurityService {

    public String getCurrentUserEmail() {
        return getAuthenticationOrThrow().getName();
    }

    public boolean hasAnyRole(List<String> roles) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roles::contains);
    }

    public boolean hasRole(String role) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    public String getRolesOfUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    public String getPermissionsFromRoles(String roles) {
        final var roleList = Arrays.stream(roles.split(","))
                .map(String::trim)
                .toList();
        log.info("[SecurityService:getPermissionsFromRoles] Roles: {}", roleList);

        final Set<Authority> authorities = Roles.getPermissionsForRoles(roleList);
        return authorities.stream()
                .map(Enum::name)
                .collect(Collectors.joining(" "));
    }

    private Collection<SimpleGrantedAuthority> getCurrentUserRoles() {
        final Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        return authentication.map(authentication1 ->
                        authentication1.getAuthorities()
                                .stream()
                                .filter(SimpleGrantedAuthority.class::isInstance)
                                .map(SimpleGrantedAuthority.class::cast)
                                .toList())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No authenticated user found"));
    }

    private Authentication getAuthenticationOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("No authenticated user found in SecurityContext");

        return authentication;
    }

}
