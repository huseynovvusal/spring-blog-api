package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    List<RefreshToken> findAllByUser_IdAndRevokedFalseAndExpiresAtAfter(Long userId, Instant now);
}
