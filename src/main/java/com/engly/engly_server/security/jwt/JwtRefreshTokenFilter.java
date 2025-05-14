package com.engly.engly_server.security.jwt;

import com.engly.engly_server.exception.ApiErrorResponse;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class JwtRefreshTokenFilter extends OncePerRequestFilter {
    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenRepo refreshTokenRepo;

    public JwtRefreshTokenFilter(RSAKeyRecord rsaKeyRecord, JwtTokenUtils jwtTokenUtils, RefreshTokenRepo refreshTokenRepo) {
        this.rsaKeyRecord = rsaKeyRecord;
        this.jwtTokenUtils = jwtTokenUtils;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("[JwtRefreshTokenFilter:doFilterInternal] :: Started ");
            log.info("[JwtRefreshTokenFilter:doFilterInternal]Filtering the Http Request:{}", request.getRequestURI());
            String token = getString(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            final JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
            final Jwt jwtRefreshToken = jwtDecoder.decode(token);
            final String userName = jwtTokenUtils.getUserName(jwtRefreshToken);

            if (!userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                final var isRefreshTokenValidInDatabase = refreshTokenRepo.findByRefreshTokenAndRevokedIsFalse(jwtRefreshToken.getTokenValue())
                        .orElse(null);
                UserDetails userDetails = jwtTokenUtils.userDetails(userName);
                if (jwtTokenUtils.isTokenValid(jwtRefreshToken, userDetails) && isRefreshTokenValidInDatabase != null) {
                    final List<GrantedAuthority> authorities = new LinkedList<>(userDetails.getAuthorities());

                    final var scope = jwtRefreshToken.getClaimAsString("scope");
                    if (FieldUtil.isValid(scope))
                        authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope.toUpperCase()));


                    final UsernamePasswordAuthenticationToken createdToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );
                    createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    final var securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(createdToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            log.info("[JwtRefreshTokenFilter:doFilterInternal] Completed");
        } catch (JwtValidationException jwtValidationException) {
            log.error("[JwtRefreshTokenFilter:doFilterInternal] Exception due to :{}", jwtValidationException.getMessage());
            new ApiErrorResponse("Session Problem", HttpStatus.NOT_ACCEPTABLE.value(),
                    jwtValidationException.getMessage(), LocalDateTime.now())
                    .responseConfiguration(response)
                    .throwException(response.getOutputStream());
        }

        filterChain.doFilter(request, response);
    }

    private static String getString(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
