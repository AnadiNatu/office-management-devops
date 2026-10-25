package com.office_management.notification_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {

        Object userId = extractAllClaims(token).get("userId");

        if (userId == null) {
            return null;
        }

        if (userId instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(
                userId.toString()
        );
    }

    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    public String extractTokenType(String token) {
        Object tokenType = extractAllClaims(token).get("tokenType");

        return tokenType != null ? tokenType.toString() : null;
    }

    public boolean isTokenValid(String token) {

        try {
            Claims claims = extractAllClaims(token);

            String tokenType = claims.get("tokenType", String.class);

            // Only ACCESS tokens should be accepted by protected REST APIs.
            if (!"ACCESS".equals(tokenType)) {
                return false;
            }
            return (claims.getSubject() != null) && (claims.get("userId") != null) ;
        } catch (Exception ex) {
            return false;
        }
    }
}