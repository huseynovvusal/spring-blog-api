package com.huseynovvusal.springblogapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for password reset operations.
 * Contains a message indicating the result of the reset attempt.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordResponse {

    /**
     * A human-readable message describing the outcome of the password reset.
     * Example: "Password successfully updated" or "Invalid token provided".
     */
    private String message;
}
