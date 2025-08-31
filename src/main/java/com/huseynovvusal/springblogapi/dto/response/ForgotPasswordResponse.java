package com.huseynovvusal.springblogapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for forgot password requests.
 * Contains a message indicating the result of the operation,
 * such as confirmation that a reset link has been sent.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordResponse {

    /**
     * A human-readable message describing the outcome of the forgot password request.
     */
    private String message;
}
