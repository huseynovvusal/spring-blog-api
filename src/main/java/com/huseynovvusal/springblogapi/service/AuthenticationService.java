package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.*;
import com.huseynovvusal.springblogapi.dto.response.ForgotPasswordResponse;
import com.huseynovvusal.springblogapi.dto.response.ResetPasswordResponse;
import com.huseynovvusal.springblogapi.events.ForgotPasswordEvent;
import com.huseynovvusal.springblogapi.events.ResetPasswordEvent;
import com.huseynovvusal.springblogapi.events.UserRegisteredEvent;
import com.huseynovvusal.springblogapi.model.Role;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service responsible for handling authentication-related operations
 * such as registration, login, password reset, and token generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${client.app.url}")
    private String clientUrl;

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user and returns a JWT token.
     *
     * @param request the registration request containing user details
     * @return a response containing the generated JWT token
     */
    public RegisterResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(user.getEmail(), user.getUsername()));
        log.debug("User registered successfully: {}", user.getUsername());

        return new RegisterResponse(token);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginRequest the login credentials
     * @return a response containing the generated JWT token
     */
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            log.warn("Login failed: user not found");
            throw new UsernameNotFoundException("User not found");
        }

        String token = jwtService.generateToken(user);
        log.debug("Login successful for user: {}", user.getUsername());

        return new LoginResponse(token);
    }

    /**
     * Generates a password reset token and sends it via email.
     *
     * @param request the forgot password request containing the user's email
     * @return a response confirming the reset link was sent
     */
    public ForgotPasswordResponse generatePasswordResetToken(ForgotPasswordRequest request) {
        log.info("Generating password reset token for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", request.getEmail());
                    return new UsernameNotFoundException("User not found with email " + request.getEmail());
                });

        String resetLink = clientUrl.concat(jwtService.generateToken(user));
        eventPublisher.publishEvent(new ForgotPasswordEvent(user, resetLink));

        log.debug("Password reset link generated for user: {}", user.getEmail());
        return new ForgotPasswordResponse("Reset link has been sent to your registered email " + user.getEmail());
    }

    /**
     * Verifies the reset token and updates the user's password.
     *
     * @param request the reset password request containing token and new password
     * @return a response confirming the password reset
     */
    @CacheEvict(value = "users", key = "#username")
    public ResetPasswordResponse verifyAndResetPassword(ResetPasswordRequest request) {
        log.info("Verifying reset token and updating password");

        String username = jwtService.extractUsername(request.getToken());
        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.warn("User not found for token username: {}", username);
            throw new UsernameNotFoundException("User not found");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        eventPublisher.publishEvent(new ResetPasswordEvent(user));

        log.debug("Password reset successful for user: {}", user.getUsername());
        return new ResetPasswordResponse("Password Reset success");
    }
}
