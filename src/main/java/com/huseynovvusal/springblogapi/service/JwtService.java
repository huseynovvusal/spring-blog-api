package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${security.jwt.secret_key}")
    private String secret;

    @Value("${security.jwt.expiration_time:259200000}")
    private long expirationSeconds;

    public String generateToken(User user) {
        // If you store roles as an enum on User (e.g., Role.USER),
        // expose it as authorities:
        List<SimpleGrantedAuthority> authorities = user.getRole() == null
                ? List.of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return generateToken(user.getId(), user.getUsername(), authorities);
    }

    // ------- token creation (uid + roles in claims) -------
    public String generateToken(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("uname", username);
        if (authorities != null) {
            claims.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList());
        }
        return createToken(claims, username);
    }

    public String createToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(getSigningKey(), Jwts.SIG.HS256) // 0.12.x API
                .compact();
    }


    public boolean isTokenValid(String token) {
        try {
            parseAllClaims(token); // throws if bad/expired
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object val = parseAllClaims(token).get("uid");
        return (val instanceof Number n) ? n.longValue() : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = parseAllClaims(token).get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
