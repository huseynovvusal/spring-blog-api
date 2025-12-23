package com.huseynovvusal.springblogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for successful user registration.
 * Contains the authentication token issued to the newly registered user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
