package com.huseynovvusal.springblogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for successful user registration.
 * Contains the authentication token issued to the newly registered user.
 */
@Data
@AllArgsConstructor
public class RegisterResponse {

    /**
     * The short-lived access token (JWT).
     */
    private String token;

    /**
     * The long-lived refresh token (opaque).
     */
    private String refreshToken;
}
