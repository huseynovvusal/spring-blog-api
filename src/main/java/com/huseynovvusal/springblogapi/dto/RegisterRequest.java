package com.huseynovvusal.springblogapi.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Request DTO for user registration.
 * Captures essential user details required to create a new account.
 */
@Data
@Builder
public class RegisterRequest {

    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Desired username for the account.
     */
    private String username;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Password chosen by the user.
     */
    private String password;
}
