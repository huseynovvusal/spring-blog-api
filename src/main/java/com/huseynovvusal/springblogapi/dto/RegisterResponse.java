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
     * The JWT or session token returned after registration.
     * This token is used for authenticating subsequent requests.
     */
    private String token;
}
