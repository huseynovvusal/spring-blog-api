package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user registration. Captures essential user details required to create a new
 * account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  /** First name of the user. */
  @NotBlank(message = "First name is required")
  @Size(min = 1, max = 50, message = "First name must not exceed 50 characters")
  private String firstName;

  /** Last name of the user. */
  @NotBlank(message = "Last name is required")
  @Size(min = 1, max = 50, message = "Last name must not exceed 50 characters")
  private String lastName;

  /** Desired username for the account. */
  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
  private String username;

  /** Email address of the user. */
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  /** Password chosen by the user. */
  @NotBlank(message = "Password is required")
  @Size(
      min = 8,
      max = 72,
      message = "Password must be between 8 and 72 characters long")
  private String password;
}
