package com.huseynovvusal.springblogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockUserResponse {
    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Username for the account.
     */
    private String username;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Indicates whether the user account is blocked.
     */
    private Boolean isBlocked;
}
