package com.huseynovvusal.springblogapi.dto;

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
    private String username;

    /**
     * The password associated with the username.
     */
    private String password;
}
