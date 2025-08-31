package com.huseynovvusal.springblogapi.dto.response;

import lombok.Value;

/**
 * A lightweight summary of user information.
 * Typically used in nested DTOs where full user details are unnecessary.
 */
@Value
public class UserSummaryDto {

    /**
     * Unique identifier of the user.
     */
    Long id;

    /**
     * Username of the user.
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
}
