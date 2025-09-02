package com.huseynovvusal.springblogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockUserRequest {
    /**
     * Username for the account.
     */
    private String username;

    /**
     * Indicates whether the user is blocked.
     */
    private Boolean isBlocked;
}
