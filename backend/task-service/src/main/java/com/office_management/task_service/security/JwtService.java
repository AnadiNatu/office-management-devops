package com.office_management.task_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");

        if (userId == null) {
            throw new IllegalArgumentException("JWT does not contain userId");
        }

        return Long.valueOf(userId.toString());
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    public String extractTokenType(String token) {
        Object tokenType = extractAllClaims(token).get("tokenType");

        return tokenType != null ? tokenType.toString() : null;
    }

    public boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    public boolean isAccessToken(String token) {
        return "ACCESS".equals(extractTokenType(token));
    }

    public boolean isTokenValid(String token) {

        try {

            Claims claims = extractAllClaims(token);

            return claims.getSubject() != null
                    && claims.get("userId") != null
                    && "ACCESS".equals(claims.get("tokenType"))
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());

        } catch (Exception ex) {

            return false;
        }
    }
}