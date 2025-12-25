package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.model.RefreshToken;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.refresh.expiration_seconds:2592000}") // 30 days default
    private long refreshTtlSeconds;

    /**
     * Issue a new refresh token for the given user. Returns the raw token string in format: id.secret
     */
    @Transactional
    public String issue(User user) {
        String id = UUID.randomUUID().toString();
        String secret = randomSecret();
        String hash = passwordEncoder.encode(secret);

        RefreshToken entity = new RefreshToken();
        entity.setId(id);
        entity.setUser(user);
        entity.setSecretHash(hash);
        entity.setExpiresAt(Instant.now().plusSeconds(refreshTtlSeconds));
        entity.setRevoked(false);
        refreshTokenRepository.save(entity);

        return String.join(".", id, secret);
    }

    /**
     * Validate the provided refresh token and return the owning user if valid.
     */
    @Transactional(readOnly = true)
    public Optional<User> validateAndGetUser(String rawToken) {
        Parsed parsed = parse(rawToken);
        return refreshTokenRepository.findById(parsed.id)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .filter(t -> passwordEncoder.matches(parsed.secret, t.getSecretHash()))
                .map(RefreshToken::getUser);
    }

    /**
     * Rotate a valid refresh token: revoke old and issue a new one for same user.
     */
    @Transactional
    public Optional<String> rotate(String rawToken) {
        Parsed parsed = parse(rawToken);
        return refreshTokenRepository.findById(parsed.id)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .filter(t -> passwordEncoder.matches(parsed.secret, t.getSecretHash()))
                .map(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    return issue(token.getUser());
                });
    }

    /** Revoke all active refresh tokens for a user. */
    @Transactional
    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.findAllByUser_IdAndRevokedFalseAndExpiresAtAfter(userId, Instant.now())
                .forEach(t -> {
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                });
    }

    private String randomSecret() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Parsed parse(String token) {
        int dot = token.indexOf('.');
        if (dot <= 0 || dot == token.length() - 1) {
            throw new IllegalArgumentException("Invalid refresh token format");
        }
        return new Parsed(token.substring(0, dot), token.substring(dot + 1));
    }

    private record Parsed(String id, String secret) {}
}
