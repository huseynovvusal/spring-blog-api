package com.huseynovvusal.springblogapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huseynovvusal.springblogapi.dto.response.UserResponseDto;
import com.huseynovvusal.springblogapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  private UserResponseDto testUserResponseDto;

  @BeforeEach
  void setUp() {
    testUserResponseDto = new UserResponseDto(1L, "testuser", "Test", "User", "test@example.com");
  }

  @Test
  @DisplayName("Should return current user profile successfully")
  void testMeSuccess() {
    // Arrange
    when(userService.getCurrentUserProfile()).thenReturn(testUserResponseDto);

    // Act
    UserResponseDto result = userController.me();

    // Assert
    assertNotNull(result);
    assertEquals(testUserResponseDto.getId(), result.getId());
    assertEquals("testuser", result.getUsername());
    assertEquals("Test", result.getFirstName());
    assertEquals("User", result.getLastName());
    assertEquals("test@example.com", result.getEmail());

    verify(userService, times(1)).getCurrentUserProfile();
  }

  @Test
  @DisplayName("Should throw UsernameNotFoundException when user not authenticated")
  void testMeThrowsExceptionWhenNotAuthenticated() {
    // Arrange
    when(userService.getCurrentUserProfile())
        .thenThrow(new UsernameNotFoundException("No authenticated user found"));

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> userController.me());
    verify(userService, times(1)).getCurrentUserProfile();
  }

  @Test
  @DisplayName("Should return user profile with correct fields")
  void testMeResponseContainsCorrectFields() {
    // Arrange
    UserResponseDto expected =
        new UserResponseDto(5L, "john_doe", "John", "Doe", "john@example.com");
    when(userService.getCurrentUserProfile()).thenReturn(expected);

    // Act
    UserResponseDto result = userController.me();

    // Assert
    assertEquals(5L, result.getId());
    assertEquals("john_doe", result.getUsername());
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertEquals("john@example.com", result.getEmail());
  }

  @Test
  @DisplayName("Should not expose password in response")
  void testMeDoesNotIncludePassword() {
    // Arrange
    when(userService.getCurrentUserProfile()).thenReturn(testUserResponseDto);

    // Act
    UserResponseDto result = userController.me();

    // Assert - UserResponseDto should not have password field
    assertNotNull(result);
    assertTrue(
        result.getClass().getDeclaredFields().length
            == 5); // Only 5 fields: id, username, firstName, lastName, email
    assertFalse(
        java.util.Arrays.stream(result.getClass().getDeclaredFields())
            .anyMatch(f -> f.getName().equals("password")));
  }
}
