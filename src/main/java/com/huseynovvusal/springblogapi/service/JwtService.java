package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating and validating JWT tokens.
 */
@Slf4j
@Service
public class JwtService {

    @Value("${security.jwt.secret_key}")
    private String secret;

    @Value("${security.jwt.expiration_time:259200000}") // default: 3 days
    private long expirationSeconds;

    /**
     * Generates a JWT token for the given user.
     *
     * @param user the user object
     * @return signed JWT token
     */
    public String generateToken(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRole() == null
                ? List.of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return generateToken(user.getId(), user.getUsername(), authorities);
    }

    /**
     * Generates a JWT token with custom claims.
     *
     * @param userId     user ID
     * @param username   username
     * @param authorities granted roles/authorities
     * @return signed JWT token
     */
    public String generateToken(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("uname", username);
        if (authorities != null) {
            claims.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList());
        }
        return createToken(claims, username);
    }

    /**
     * Creates a JWT token with the given claims and subject.
     *
     * @param claims  custom claims
     * @param subject token subject (usually username)
     * @return signed JWT token
     */
    public String createToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validates the token's signature and expiration.
     *
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            parseAllClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the username (subject) from the token.
     *
     * @param token JWT token
     * @return username
     */
    public String extractUsername(String token) {
        return parseAllClaims(token).getSubject();
    }

    /**
     * Extracts the user ID from the token.
     *
     * @param token JWT token
     * @return user ID or null
     */
    public Long extractUserId(String token) {
        Object val = parseAllClaims(token).get("uid");
        return (val instanceof Number n) ? n.longValue() : null;
    }

    /**
     * Extracts roles from the token.
     *
     * @param token JWT token
     * @return list of role strings
     */
    public List<String> extractRoles(String token) {
        Object roles = parseAllClaims(token).get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Parses all claims from the token.
     *
     * @param token JWT token
     * @return claims object
     */
    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Decodes and returns the signing key.
     *
     * @return HMAC secret key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
