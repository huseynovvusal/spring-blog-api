package com.huseynovvusal.springblogapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.huseynovvusal.springblogapi.dto.LoginRequest;
import com.huseynovvusal.springblogapi.dto.LoginResponse;
import com.huseynovvusal.springblogapi.dto.RegisterRequest;
import com.huseynovvusal.springblogapi.dto.RegisterResponse;
import com.huseynovvusal.springblogapi.dto.ResetPasswordRequest;
import com.huseynovvusal.springblogapi.dto.response.ResetPasswordResponse;
import com.huseynovvusal.springblogapi.events.ResetPasswordEvent;
import com.huseynovvusal.springblogapi.events.UserRegisteredEvent;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private JwtService jwtService;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private RefreshTokenService refreshTokenService;
  private AuthenticationService authenticationService;

  @BeforeEach
  void setup() {
  authenticationService = new AuthenticationService(eventPublisher, userRepository, passwordEncoder, jwtService,
    authenticationManager, refreshTokenService);
  }

  @Test
  void should_return_registration_response_with_token() {
    // Given
    String token = "token";
    RegisterRequest request = RegisterRequest.builder()
        .email("email")
        .lastName("lastName")
        .username("username")
        .firstName("firstName")
        .build();

    // When
    when(passwordEncoder.encode(any())).thenReturn("password");
    when(userRepository.save(any())).thenReturn(new User());
    when(jwtService.generateToken(any())).thenReturn(token);
  when(refreshTokenService.issue(any())).thenReturn("refresh");
    doNothing().when(eventPublisher).publishEvent(any(UserRegisteredEvent.class));

    RegisterResponse resultRegisterResponse = authenticationService.register(request);

    // Then
    assertThat(resultRegisterResponse.getToken()).isEqualTo(token);
  }

  @Test
  void should_throw_username_not_found_exception_when_login() {
    // Given

    // When
    when(authenticationManager.authenticate(any())).thenReturn(null);
    when(userRepository.findByUsername(any())).thenReturn(null);
    LoginRequest loginRequest = new LoginRequest();

    // Then
    var e = assertThrows(UsernameNotFoundException.class, () -> authenticationService.login(loginRequest));
    assertThat(e.getMessage()).isEqualTo("User not found");
  }

  @Test
  void should_return_login_response_with_token() {
    // Given
    String token = "token";
    LoginRequest loginRequest = new LoginRequest("username", "password");
    User user = new User();

    // When
    when(authenticationManager.authenticate(any())).thenReturn(null);
    when(userRepository.findByUsername(any())).thenReturn(user);
    when(jwtService.generateToken(any())).thenReturn(token);
  when(refreshTokenService.issue(any())).thenReturn("refresh");

    LoginResponse loginResponse = authenticationService.login(loginRequest);

    // Then
    assertThat(loginResponse.getToken()).isEqualTo(token);
  }

  @Test
  void should_throw_username_not_found_exception_when_verify_and_reset_password() {
    // Given
    ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();

    // When
    when(jwtService.extractUsername(any())).thenReturn(null);
    when(userRepository.findByUsername(any())).thenReturn(null);

    // Then
    var e = assertThrows(UsernameNotFoundException.class,
        () -> authenticationService.verifyAndResetPassword(resetPasswordRequest));
    assertThat(e.getMessage()).isEqualTo("User not found");
  }

  @Test
  void should_return_reset_password_response() {
    // Given
    ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
    String username = "username";
    User user = new User();

    // When
    when(jwtService.extractUsername(any())).thenReturn(username);
    when(userRepository.findByUsername(any())).thenReturn(user);
    when(passwordEncoder.encode(any())).thenReturn("password");
    when(userRepository.save(any())).thenReturn(user);
    doNothing().when(eventPublisher).publishEvent(any(ResetPasswordEvent.class));
  doNothing().when(refreshTokenService).revokeAllForUser(anyLong());

    ResetPasswordResponse resetPasswordResponse = authenticationService.verifyAndResetPassword(resetPasswordRequest);

    // Then
    assertThat(resetPasswordResponse.getMessage()).isEqualTo("Password Reset success");
  }

}