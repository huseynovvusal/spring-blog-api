package com.huseynovvusal.springblogapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huseynovvusal.springblogapi.dto.BlockUserRequest;
import com.huseynovvusal.springblogapi.dto.BlockUserResponse;
import com.huseynovvusal.springblogapi.dto.response.UserResponseDto;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Autowired private UserService userService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(userRepository);
  }

  @Test
  void shouldBlockUserWhenRequested() {
    // Given
    String username = "user1";
    User user = new User();
    user.setUsername(username);
    user.setBlocked(false);

    when(userRepository.findByUsername(username)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);

    // When
    BlockUserRequest request = new BlockUserRequest(username, true);
    BlockUserResponse response = userService.changeBlockStatus(request);

    // Then
    assertTrue(response.getIsBlocked());
    assertThat(response.getUsername()).isEqualTo(username);
    verify(userRepository).save(user);
  }

  @Test
  void shouldUnblockUserWhenRequested() {
    // Given
    String username = "user2";
    User user = new User();
    user.setUsername(username);
    user.setBlocked(true);

    when(userRepository.findByUsername(username)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);

    // When
    BlockUserRequest request = new BlockUserRequest(username, false);
    BlockUserResponse response = userService.changeBlockStatus(request);

    // Then
    assertFalse(response.getIsBlocked());
    assertThat(response.getUsername()).isEqualTo(username);
    verify(userRepository).save(user);
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    // Given
    String username = "notfound";
    when(userRepository.findByUsername(username)).thenReturn(null);

    // When & Then
    BlockUserRequest request = new BlockUserRequest(username, true);
    assertThrows(UsernameNotFoundException.class, () -> userService.changeBlockStatus(request));
  }

  // Additional test for getcurrentUserProfile

  @Test
  @DisplayName("Should retrieve current user profile successfully")
  void testGetCurrentUserProfileSuccess() {
    // Arrange
    User testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setFirstName("Test");
    testUser.setLastName("User");
    testUser.setEmail("test@example.com");

    when(userRepository.findByUsername("testuser")).thenReturn(testUser);
    mockSecurityContext("testuser");

    // Act
    UserResponseDto result = userService.getCurrentUserProfile();

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("testuser", result.getUsername());
    assertEquals("Test", result.getFirstName());
    assertEquals("User", result.getLastName());
    assertEquals("test@example.com", result.getEmail());

    verify(userRepository, times(1)).findByUsername("testuser");
  }

  @Test
  @DisplayName("Should throw UsernameNotFoundException when user not found")
  void testGetCurrentUserProfileUserNotFound() {
    // Arrange
    when(userRepository.findByUsername("nonexistent")).thenReturn(null);
    mockSecurityContext("nonexistent");

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUserProfile());
    verify(userRepository, times(1)).findByUsername("nonexistent");
  }

  @Test
  @DisplayName("Should throw UsernameNotFoundException when not authenticated")
  void testGetCurrentUserProfileNotAuthenticated() {
    // Arrange
    mockSecurityContextNull();

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUserProfile());
    verify(userRepository, never()).findByUsername(anyString());
  }

  private void mockSecurityContext(String username) {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn(username);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext); // Direct call, not mockStatic
  }

  private void mockSecurityContextNull() {
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(null);
    SecurityContextHolder.setContext(securityContext);
  }
}
