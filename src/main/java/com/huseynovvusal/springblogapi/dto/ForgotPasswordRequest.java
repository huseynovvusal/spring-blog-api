package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Request DTO for initiating a password reset.
 * Contains the user's email address, which must be valid and non-empty.
 */
@Data
@Builder
public class ForgotPasswordRequest {

    /**
     * The email address of the user requesting a password reset.
     * Must be a valid format and not blank.
     */
    @NotBlank(message = "Email Required")
    @Email(message = "Invalid email format")
    private String email;
}
