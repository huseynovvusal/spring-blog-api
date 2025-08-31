package com.huseynovvusal.springblogapi.dto.response;

import lombok.Value;

/**
 * Data Transfer Object representing a user's public profile information.
 * Typically used in API responses where user identity needs to be exposed safely.
 */
@Value
public class UserResponseDto {

    /**
     * Unique identifier of the user.
     */
    Long id;

    /**
     * Username chosen by the user.
     */
    String username;

    /**
     * First name of the user.
     */
    String firstName;

    /**
     * Last name of the user.
     */
    String lastName;

    /**
     * Email address of the user.
     */
    String email;
}
