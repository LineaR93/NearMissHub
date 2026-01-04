package com.epicode.NearMissHub.security;

// JWT-based security (stateless). JwtFilter reads the token and sets the SecurityContext.

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtTools {

    private final Key key;

    public JwtTools(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String userId, String role) {
        // I keep the token small: sub = user id, role = single string.
        // Expiration is 1 hour, enough for the exam demo without storing sessions.
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    public Claims verifyAndGetClaims(String token) {
        // If signature/expiration is invalid, jjwt throws and JwtFilter will clear the SecurityContext.
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }




}
