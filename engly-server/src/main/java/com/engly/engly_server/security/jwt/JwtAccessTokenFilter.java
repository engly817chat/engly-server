package com.engly.engly_server.security.jwt;

import com.engly.engly_server.exception.ApiErrorResponse;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class JwtAccessTokenFilter extends OncePerRequestFilter {
    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtAccessTokenFilter(RSAKeyRecord rsaKeyRecord, JwtTokenUtils jwtTokenUtils) {
        this.rsaKeyRecord = rsaKeyRecord;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {

        try {
            log.info("[JwtAccessTokenFilter:doFilterInternal] :: Started ");

            log.info("[JwtAccessTokenFilter:doFilterInternal]Filtering the Http Request:{}", request.getRequestURI());

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();

            if (authHeader == null || !authHeader.startsWith(TokenType.Bearer.name())) {
                filterChain.doFilter(request, response);
                return;
            }

            final String token = authHeader.substring(7);
            final Jwt jwtToken = jwtDecoder.decode(token);


            final String userName = jwtTokenUtils.getUserName(jwtToken);

            if (!userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = jwtTokenUtils.userDetails(userName);
                if (jwtTokenUtils.isTokenValid(jwtToken, userDetails)) {
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken createdToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(createdToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            log.info("[JwtAccessTokenFilter:doFilterInternal] Completed");

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("[JwtAccessTokenFilter:doFilterInternal] Exception due to :{}", e.getMessage());
            new ApiErrorResponse("Session Problem", HttpStatus.NOT_ACCEPTABLE.value(),
                    e.getMessage(), LocalDateTime.now())
                    .responseConfiguration(response)
                    .throwException(response.getOutputStream());
        }
    }
}
