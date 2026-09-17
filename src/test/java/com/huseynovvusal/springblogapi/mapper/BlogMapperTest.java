package com.huseynovvusal.springblogapi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BlogMapper Tests")
class BlogMapperTest {

  @Test
  @DisplayName("Should return null when Blog is null")
  void testToDtoWithNullBlog() {
    // Act
    var result = BlogMapper.toDto(null, 0L);

    // Assert
    assertNull(result);
  }

  @Test
  void testToDtoSuccess() {
    // Arrange
    User author = new User();
    author.setId(1L);
    author.setUsername("john_doe");
    author.setFirstName("John");
    author.setLastName("Doe");

    Date createdAt = new Date();
    Date updatedAt = new Date();

    Blog blog = new Blog();
    blog.setId(10L);
    blog.setTitle("Test Blog");
    blog.setContent("Test Content");
    blog.setAuthor(author);
    blog.setCreatedAt(createdAt);
    blog.setUpdatedAt(updatedAt);
    blog.setViews(25L);

    Long likeCount = 7L;

    // Act
    BlogResponseDto result = BlogMapper.toDto(blog, likeCount);

    // Assert
    assertNotNull(result);
    assertEquals(10L, result.getId());
    assertEquals("Test Blog", result.getTitle());
    assertEquals("Test Content", result.getContent());
    assertEquals(createdAt, result.getCreatedAt());
    assertEquals(updatedAt, result.getUpdatedAt());
    assertEquals(25L, result.getViews());
    assertEquals(7L, result.getLikeCount());

    assertNotNull(result.getAuthor());
    assertEquals(1L, result.getAuthor().getId());
    assertEquals("john_doe", result.getAuthor().getUsername());
    assertEquals("John", result.getAuthor().getFirstName());
    assertEquals("Doe", result.getAuthor().getLastName());
  }

  @Test
  @DisplayName("Should return null when User is null")
  void testToUserSummaryWithNullUser() {
    // Act
    var result = BlogMapper.toUserSummary(null);

    // Assert
    assertNull(result);
  }

  @Test
  @DisplayName("Should convert User to UserSummaryDto successfully")
  void testToUserSummarySuccess() {
    // Arrange
    User user = new User();
    user.setId(2L);
    user.setUsername("jane_doe");
    user.setFirstName("Jane");
    user.setLastName("Doe");

    // Act
    var result = BlogMapper.toUserSummary(user);

    // Assert
    assertNotNull(result);
    assertEquals(2L, result.getId());
    assertEquals("jane_doe", result.getUsername());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Doe", result.getLastName());
  }
}
