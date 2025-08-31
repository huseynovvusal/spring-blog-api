package com.huseynovvusal.springblogapi.events;

import com.huseynovvusal.springblogapi.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Event triggered when a user requests a password reset.
 * Carries the user entity and the generated reset link to be used by listeners.
 */
@Data
@AllArgsConstructor
public class ForgotPasswordEvent {

    /**
     * The user who requested the password reset.
     */
    private User user;

    /**
     * The reset link generated for the user to reset their password.
     */
    private String resetLink;
}
