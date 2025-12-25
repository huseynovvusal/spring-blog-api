package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for resetting a user's password.
 * Contains the reset token and the new password to be applied.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    /**
     * The token used to verify the password reset request.
     * Typically sent to the user's email.
     */
    @NotBlank(message = "Token cannot be blank")
    private String token;

    /**
     * The new password to be set for the user.
     * Must not be blank and should meet minimum security requirements.
     */
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
