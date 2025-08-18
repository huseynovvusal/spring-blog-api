package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "Email Required")
    @Email(message = "Invalid email format")
    private String email;
}
