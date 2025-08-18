package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotNull(message = "Token cannot be null")
    @NotBlank(message = "Token cannot be blank")
    private String token;

    @NotNull(message = "New password cannot be null")
    @NotBlank(message = "New password cannot be blank")
    private String newPassword;
}
