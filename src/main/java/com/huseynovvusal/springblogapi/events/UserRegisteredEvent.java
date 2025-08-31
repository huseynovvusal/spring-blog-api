package com.huseynovvusal.springblogapi.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Event triggered when a new user successfully registers.
 * Carries basic user information for downstream listeners (e.g., sending welcome emails).
 */
@Data
@AllArgsConstructor
public class UserRegisteredEvent {

    /**
     * The email address of the newly registered user.
     */
    private String email;

    /**
     * The username chosen by the user during registration.
     */
    private String username;
}
