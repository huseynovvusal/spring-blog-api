package com.huseynovvusal.springblogapi.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.dto.response.UserSummaryDto;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;

class BlogMapperTest {

  @Test
  void toDtoReturnsNullForNullBlog() {
    BlogResponseDto dto = BlogMapper.toDto(null, 5L);
    assertThat(dto).isNull();
  }

  @Test
  void toDtoMapsAllFieldsIncludingAuthorAndLikeCount() {
    User author = new User();
    author.setId(1L);
    author.setUsername("jane");
    author.setFirstName("Jane");
    author.setLastName("Doe");

    Date createdAt = new Date(1_000_000L);
    Date updatedAt = new Date(2_000_000L);

    Blog blog = new Blog();
    blog.setId(10L);
    blog.setTitle("Hello");
    blog.setContent("World");
    blog.setCreatedAt(createdAt);
    blog.setUpdatedAt(updatedAt);
    blog.setAuthor(author);
    blog.setViews(42L);

    Long likeCount = 7L;

    BlogResponseDto dto = BlogMapper.toDto(blog, likeCount);

    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isEqualTo(10L);
    assertThat(dto.getTitle()).isEqualTo("Hello");
    assertThat(dto.getContent()).isEqualTo("World");
    assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(dto.getViews()).isEqualTo(42L);
    assertThat(dto.getLikeCount()).isEqualTo(7L);

    assertThat(dto.getAuthor()).isNotNull();
    assertThat(dto.getAuthor().getId()).isEqualTo(1L);
    assertThat(dto.getAuthor().getUsername()).isEqualTo("jane");
    assertThat(dto.getAuthor().getFirstName()).isEqualTo("Jane");
    assertThat(dto.getAuthor().getLastName()).isEqualTo("Doe");
  }

  @Test
  void toUserSummaryReturnsNullForNullUser() {
    UserSummaryDto summary = BlogMapper.toUserSummary(null);
    assertThat(summary).isNull();
  }

  @Test
  void toUserSummaryMapsAllFields() {
    User user = new User();
    user.setId(5L);
    user.setUsername("john");
    user.setFirstName("John");
    user.setLastName("Smith");

    UserSummaryDto summary = BlogMapper.toUserSummary(user);

    assertThat(summary).isNotNull();
    assertThat(summary.getId()).isEqualTo(5L);
    assertThat(summary.getUsername()).isEqualTo("john");
    assertThat(summary.getFirstName()).isEqualTo("John");
    assertThat(summary.getLastName()).isEqualTo("Smith");
  }
}