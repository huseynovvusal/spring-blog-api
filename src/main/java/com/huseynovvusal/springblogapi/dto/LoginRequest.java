package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user login.
 * Contains the username and password submitted by the client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    /**
     * The username of the user attempting to log in.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password associated with the username.
     */
    @NotBlank(message = "Password is required")
    private String password;
}
