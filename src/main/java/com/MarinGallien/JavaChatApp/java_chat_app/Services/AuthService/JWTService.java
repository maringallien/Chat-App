package com.MarinGallien.JavaChatApp.java_chat_app.Services.AuthService;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    // Inject secret key from application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Read token expiry time from application.properties
    @Value("${jwt.expiration-hours:24}")
    private long tokenExpirationHours;

    // Generate JWT token for authenticated user
    public String generateToken(String userId, String username) {
        try {
            // Set expiration date
            Instant now = Instant.now();
            Instant expiration = now.plus(tokenExpirationHours, ChronoUnit.HOURS);

            // Convert secret key to cryptographic key
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

            // Create and return JWT
            return Jwts.builder()
                    .subject(userId)
                    .claim("username", username)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(expiration))
                    .signWith(key, Jwts.SIG.HS256)
                    .compact();

        } catch (Exception e) {
            logger.error("Error generating JWT token for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    // Extract used ID from JWT token
    public String extractUserId(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject();

        } catch (Exception e) {
            logger.error("Failed to extract user ID from token: {}", e.getMessage());
            return null;
        }
    }

    // Extract username from JWT token
    public String extractUsername(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("username", String.class);
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token has expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("JWT token is malformed: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT token is invalid: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error validating JWT token: {}", e.getMessage());
            return false;
        }
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // If token cannot be parsed, consider it expired
            return true;
        }
    }

    // Retrieves token's expiration date
    public Date getExpirationDate(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("Error extracting expiration date from JWT token: {}", e.getMessage());
            return null;
        }
    }

    // Extracts all claims from JWT token
    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extracts token from authorization header
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
