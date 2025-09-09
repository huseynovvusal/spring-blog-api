package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.*;
import com.huseynovvusal.springblogapi.dto.response.ForgotPasswordResponse;
import com.huseynovvusal.springblogapi.dto.response.ResetPasswordResponse;
import com.huseynovvusal.springblogapi.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling authentication-related endpoints.
 * Includes registration, login, password reset link generation, and password resetting.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    /**
     * Registers a new user with the provided credentials.
     *
     * @param request the registration request containing user details
     * @return a response entity with registration status and user info
     */
    @PostMapping("register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Received registration request for email: {}", request.getEmail());
        RegisterResponse response = authenticationService.register(request);
        logger.debug("Registration successful for email: {}, response: {}", request.getEmail(), response);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user and returns a JWT token if credentials are valid.
     *
     * @param request the login request containing username and password
     * @return a response entity with authentication token and user info
     */
    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());
        LoginResponse response = authenticationService.login(request);
        logger.debug("Login successful for username: {}, response: {}", request.getUsername(), response);
        return ResponseEntity.ok(response);
    }

    /**
     * Generates a password reset token and sends it to the user's email.
     *
     * @param request the forgot password request containing the user's email
     * @return a response entity with token generation status
     */
    @PostMapping("forgot-password")
    public ResponseEntity<ForgotPasswordResponse> getPasswordResetLink(@Valid @RequestBody ForgotPasswordRequest request){
        logger.info("Password reset link requested for email: {}", request.getEmail());
        ForgotPasswordResponse response = authenticationService.generatePasswordResetToken(request);
        logger.debug("Password reset token generated for email: {}, response: {}", request.getEmail(), response);
        return ResponseEntity.ok(response);
    }

    /**
     * Verifies the password reset token and updates the user's password.
     *
     * @param request the reset password request containing token and new password
     * @return a response entity with password reset status
     */
    @PostMapping("reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        logger.info("Password reset attempt with token: {}", request.getToken());
        ResetPasswordResponse response = authenticationService.verifyAndResetPassword(request);
        logger.debug("Password reset completed for token: {}, response: {}", request.getToken(), response);
        return ResponseEntity.ok(response);
    }
}
