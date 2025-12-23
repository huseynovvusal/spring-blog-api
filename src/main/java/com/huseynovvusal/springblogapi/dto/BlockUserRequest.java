package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockUserRequest {
    /**
     * Username for the account.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * Indicates whether the user is blocked.
     */
    @NotNull(message = "isBlocked is required")
    private Boolean isBlocked;
}
