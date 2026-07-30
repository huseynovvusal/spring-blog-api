package com.huseynovvusal.springblogapi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.dto.response.UserSummaryDto;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BlogMapper Tests")
class BlogMapperTest {

  private Blog blog;
  private User author;
  private Date createdAt;
  private Date updatedAt;

  @BeforeEach
  void setUp() {
    author = new User();
    author.setId(7L);
    author.setUsername("jane_doe");
    author.setFirstName("Jane");
    author.setLastName("Doe");

    createdAt = new Date(1_700_000_000_000L);
    updatedAt = new Date(1_700_000_100_000L);

    blog = new Blog();
    blog.setId(42L);
    blog.setTitle("Testing BlogMapper");
    blog.setContent("Mapper unit-test content");
    blog.setCreatedAt(createdAt);
    blog.setUpdatedAt(updatedAt);
    blog.setAuthor(author);
    blog.setViews(125L);
  }

  @Test
  @DisplayName("Should map all Blog fields and the supplied like count")
  void shouldMapAllBlogFieldsAndLikeCount() {
    BlogResponseDto result = BlogMapper.toDto(blog, 18L);

    assertNotNull(result);
    assertEquals(42L, result.getId());
    assertEquals("Testing BlogMapper", result.getTitle());
    assertEquals("Mapper unit-test content", result.getContent());
    assertEquals(createdAt, result.getCreatedAt());
    assertEquals(updatedAt, result.getUpdatedAt());
    assertEquals(125L, result.getViews());
    assertEquals(18L, result.getLikeCount());
  }

  @Test
  @DisplayName("Should map Blog author to UserSummaryDto")
  void shouldMapAuthorToUserSummary() {
    BlogResponseDto result = BlogMapper.toDto(blog, 0L);

    assertNotNull(result);
    assertNotNull(result.getAuthor());
    assertEquals(7L, result.getAuthor().getId());
    assertEquals("jane_doe", result.getAuthor().getUsername());
    assertEquals("Jane", result.getAuthor().getFirstName());
    assertEquals("Doe", result.getAuthor().getLastName());
  }

  @Test
  @DisplayName("Should preserve a null like count")
  void shouldPreserveNullLikeCount() {
    BlogResponseDto result = BlogMapper.toDto(blog, null);

    assertNotNull(result);
    assertNull(result.getLikeCount());
  }

  @Test
  @DisplayName("Should return null when Blog is null")
  void shouldReturnNullWhenBlogIsNull() {
    assertNull(BlogMapper.toDto(null, 5L));
  }

  @Test
  @DisplayName("Should map a Blog with no author")
  void shouldMapBlogWithNullAuthor() {
    blog.setAuthor(null);

    BlogResponseDto result = BlogMapper.toDto(blog, 3L);

    assertNotNull(result);
    assertNull(result.getAuthor());
  }

  @Test
  @DisplayName("Should map User fields to UserSummaryDto")
  void shouldMapUserFieldsToSummary() {
    UserSummaryDto result = BlogMapper.toUserSummary(author);

    assertNotNull(result);
    assertEquals(7L, result.getId());
    assertEquals("jane_doe", result.getUsername());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Doe", result.getLastName());
  }

  @Test
  @DisplayName("Should return null UserSummaryDto when User is null")
  void shouldReturnNullSummaryWhenUserIsNull() {
    assertNull(BlogMapper.toUserSummary(null));
  }
}
