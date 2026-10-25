package com.office_management.auth_service.security;

import com.office_management.auth_service.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    private final long accessTokenExpiration;

    private final long refreshTokenExpiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:3600000}")
            long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration:2592000000}")
            long refreshTokenExpiration) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret cannot be empty");
        }

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(Users user) {

        validateUser(user);
        Date now = new Date();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .claim("tokenType", "ACCESS")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Users user) {
        validateUser(user);
        Date now = new Date();
        return Jwts.builder().subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("tokenType", "REFRESH")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token)
                .getSubject();
    }

    public Long extractUserId(String token) {
        Object userId = getClaims(token).get("userId");
        if (userId == null) {
            return null;
        }
        return Long.valueOf(userId.toString());
    }

    public String extractRole(String token) {
        Object role = getClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    public boolean isAccessToken(String token) {
        return "ACCESS".equals(getClaims(token).get("tokenType"));
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(getClaims(token).get("tokenType"));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username != null && username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims getClaims(String token) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT token cannot be empty"
            );
        }

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void validateUser(
            Users user
    ) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null"
            );
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null"
            );
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "User email cannot be empty"
            );
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                    "User role cannot be null"
            );
        }
    }
}