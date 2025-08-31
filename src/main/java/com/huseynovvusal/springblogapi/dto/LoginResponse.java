package com.huseynovvusal.springblogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for successful login.
 * Contains the authentication token issued to the client.
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    /**
     * The JWT or session token returned after successful authentication.
     * This token is used for authorizing subsequent requests.
     */
    private String token;
}
