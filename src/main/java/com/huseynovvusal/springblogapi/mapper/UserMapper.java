package com.huseynovvusal.springblogapi.mapper;

import com.huseynovvusal.springblogapi.dto.response.UserResponseDto;
import com.huseynovvusal.springblogapi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for mapping {@link User} entities to {@link UserResponseDto} DTOs. Centralizes user
 * DTO conversion logic and provides consistent logging.
 */
public final class UserMapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserMapper.class);

  // Prevent instantiation
  private UserMapper() {}

  /**
   * Converts a {@link User} entity to a {@link UserResponseDto}.
   *
   * @param user the user entity to convert
   * @return the corresponding UserResponseDto, or null if user is null
   */
  public static UserResponseDto toResponseDto(User user) {
    if (user == null) {
      LOGGER.debug("[UserMapper] User is null, returning null");
      return null;
    }

    LOGGER.debug(
        "[UserMapper] Converting User to UserResponseDto for user: {}", user.getUsername());

    UserResponseDto response =
        new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail());

    LOGGER.debug("[UserMapper] User conversion completed for user: {}", user.getUsername());

    return response;
  }
}
