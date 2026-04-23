package com.sonora.sonora.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** Service for generating and validating JWT access tokens. */
@Service
public class JwtService {

    /** Key used to sign and verify JWT tokens. */
    private final SecretKey signingKey;

    /** JWT expiration duration in milliseconds. */
    private final long expirationMs;

    /**
     * Creates a new JWT service.
     *
     * @param secret signing secret
     * @param tokenExpirationMs expiration duration in milliseconds
     */
    public JwtService(
            @Value("${jwt.secret}") final String secret,
            @Value("${jwt.expiration-ms:86400000}")
            final long tokenExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
        );
        this.expirationMs = tokenExpirationMs;
    }

    /**
     * Generates a JWT token containing email and role claims.
     *
     * @param email user email
     * @param role user role
     * @return signed JWT token
     */
    public String generateToken(final String email, final String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts email (subject) from token.
     *
     * @param token JWT token
     * @return email subject
     */
    public String extractEmail(final String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts role claim from token.
     *
     * @param token JWT token
     * @return role claim
     */
    public String extractRole(final String token) {
        return extractClaims(token).get("role", String.class);
    }

    /**
     * Validates a JWT token signature and expiration.
     *
     * @param token JWT token
     * @return true when token is valid
     */
    public boolean isTokenValid(final String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Parses JWT claims from a token.
     *
     * @param token JWT token
     * @return parsed claims
     */
    private Claims extractClaims(final String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
