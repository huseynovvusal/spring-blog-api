package com.huseynovvusal.springblogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockUserRequest {
    /**
     * Desired username for the account.
     */
    private String username;

    /**
     * Password chosen by the user.
     */
    private Boolean isBlocked;
}
