package com.huseynovvusal.springblogapi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.huseynovvusal.springblogapi.dto.response.UserResponseDto;
import com.huseynovvusal.springblogapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserMapper Tests")
class UserMapperTest {

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("john_doe");
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setEmail("john@example.com");
    testUser.setPassword("hashedPassword");
    testUser.setBlocked(false);
  }

  @Test
  @DisplayName("Should convert User to UserResponseDto successfully")
  void testToResponseDtoSuccess() {
    // Act
    UserResponseDto result = UserMapper.toResponseDto(testUser);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("john_doe", result.getUsername());
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertEquals("john@example.com", result.getEmail());
  }

  @Test
  @DisplayName("Should not include password in converted DTO")
  void testToResponseDtoExcludesPassword() {
    // Act
    UserResponseDto result = UserMapper.toResponseDto(testUser);

    // Assert
    assertNotNull(result);
    assertFalse(
        java.util.Arrays.stream(result.getClass().getDeclaredFields())
            .anyMatch(f -> f.getName().equals("password")));
  }

  @Test
  @DisplayName("Should return null when User is null")
  void testToResponseDtoWithNullUser() {
    // Act
    UserResponseDto result = UserMapper.toResponseDto(null);

    // Assert
    assertNull(result);
  }

  @Test
  @DisplayName("Should map all required fields correctly")
  void testToResponseDtoMapsAllFields() {
    // Arrange
    testUser.setId(99L);
    testUser.setUsername("test_username");
    testUser.setFirstName("FirstName");
    testUser.setLastName("LastName");
    testUser.setEmail("test@mail.com");

    // Act
    UserResponseDto result = UserMapper.toResponseDto(testUser);

    // Assert
    assertEquals(99L, result.getId());
    assertEquals("test_username", result.getUsername());
    assertEquals("FirstName", result.getFirstName());
    assertEquals("LastName", result.getLastName());
    assertEquals("test@mail.com", result.getEmail());
  }

  @Test
  @DisplayName("Should handle user with special characters in email")
  void testToResponseDtoWithSpecialCharactersInEmail() {
    // Arrange
    testUser.setEmail("user+test@example.co.uk");

    // Act
    UserResponseDto result = UserMapper.toResponseDto(testUser);

    // Assert
    assertEquals("user+test@example.co.uk", result.getEmail());
  }
}
