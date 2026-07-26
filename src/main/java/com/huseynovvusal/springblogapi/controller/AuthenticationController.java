package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.ForgotPasswordRequest;
import com.huseynovvusal.springblogapi.dto.LoginRequest;
import com.huseynovvusal.springblogapi.dto.LoginResponse;
import com.huseynovvusal.springblogapi.dto.RefreshTokenRequest;
import com.huseynovvusal.springblogapi.dto.RegisterRequest;
import com.huseynovvusal.springblogapi.dto.RegisterResponse;
import com.huseynovvusal.springblogapi.dto.ResetPasswordRequest;
import com.huseynovvusal.springblogapi.dto.response.ForgotPasswordResponse;
import com.huseynovvusal.springblogapi.dto.response.ResetPasswordResponse;
import com.huseynovvusal.springblogapi.exception.InvalidRefreshTokenException;
import com.huseynovvusal.springblogapi.exception.UserAlreadyRegisteredException;
import com.huseynovvusal.springblogapi.service.AuthenticationService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling authentication-related endpoints. Includes registration,
 * login, password reset link generation, and password resetting.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
  private final AuthenticationService authenticationService;

  /**
   * Registers a new user with the provided credentials.
   *
   * @param request the registration request containing user details
   * @return a response entity with registration status and user info
   * @throws UserAlreadyRegisteredException
   */
  @Operation(
      summary = "Register a new user",
      description = "Creates a new account and returns registration details.")
  @PostMapping("register")
  @RateLimiter(name = "auth")
  public RegisterResponse register(@Valid @RequestBody RegisterRequest request)
      throws UserAlreadyRegisteredException {
    LOGGER.info("Received registration request for email: {}", request.getEmail());
    RegisterResponse response = authenticationService.register(request);
    LOGGER.debug(
        "Registration successful for email: {}, response: {}", request.getEmail(), response);
    return response;
  }

  /**
   * Authenticates a user and returns a JWT token if credentials are valid.
   *
   * @param request the login request containing username and password
   * @return a response entity with authentication token and user info
   */
  @Operation(
      summary = "Log in a user",
      description = "Authenticates a user and returns access credentials.")
  @PostMapping("login")
  @RateLimiter(name = "auth")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    LOGGER.info("Login attempt for username: {}", request.getUsername());
    LoginResponse response = authenticationService.login(request);
    LOGGER.debug(
        "Login successful for username: {}, response: {}", request.getUsername(), response);
    return response;
  }

  /**
   * Generates a password reset token and sends it to the user's email.
   *
   * @param request the forgot password request containing the user's email
   * @return a response entity with token generation status
   */
  @Operation(
      summary = "Request password reset",
      description = "Generates a password reset token and sends it to the user's email.")
  @PostMapping("forgot-password")
  public ForgotPasswordResponse getPasswordResetLink(
      @Valid @RequestBody ForgotPasswordRequest request) {
    LOGGER.info("Password reset link requested for email: {}", request.getEmail());
    ForgotPasswordResponse response = authenticationService.generatePasswordResetToken(request);
    LOGGER.debug(
        "Password reset token generated for email: {}, response: {}", request.getEmail(), response);
    return response;
  }

  /**
   * Verifies the password reset token and updates the user's password.
   *
   * @param request the reset password request containing token and new password
   * @return a response entity with password reset status
   */
  @Operation(
      summary = "Reset password",
      description = "Verifies the password reset token and updates the user's password.")
  @PostMapping("reset-password")
  public ResetPasswordResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    LOGGER.info("Password reset attempt with token: {}", request.getToken());
    ResetPasswordResponse response = authenticationService.verifyAndResetPassword(request);
    LOGGER.debug(
        "Password reset completed for token: {}, response: {}", request.getToken(), response);
    return response;
  }

  /**
   * Rotates a valid refresh token into a new access token and refresh token.
   *
   * @throws InvalidRefreshTokenException
   */
  @Operation(
      summary = "Refresh tokens",
      description = "Rotates a valid refresh token into new access and refresh tokens.")
  @PostMapping("refresh")
  @RateLimiter(name = "auth")
  public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request)
      throws InvalidRefreshTokenException {
    LOGGER.info("Refresh token attempt");
    return authenticationService.refreshTokens(request.getRefreshToken());
  }
}
