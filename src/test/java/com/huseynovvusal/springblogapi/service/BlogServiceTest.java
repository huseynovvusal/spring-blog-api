package com.huseynovvusal.springblogapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import com.huseynovvusal.springblogapi.repository.LikeRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlogService Unit Tests")
class BlogServiceTest {

  @Mock private BlogRepository blogRepository;
  @Mock private LikeRepository likeRepository;

  private BlogService blogService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    blogService = new BlogService(blogRepository, null, null, likeRepository);
  }

  @Test
  @DisplayName("should increment views when blog is fetched by ID")
  void shouldIncrementViewsWhenGetById() {
    // Given
    Long blogId = 1L;
    Blog blog = new Blog();
    blog.setId(blogId);
    blog.setTitle("Test Blog");
    blog.setContent("Test Content");
    blog.setViews(0L);

    User author = new User();
    author.setId(1L);
    author.setUsername("testuser");
    blog.setAuthor(author);

    when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
    when(likeRepository.countByBlog_Id(blogId)).thenReturn(0L);

    // When
    BlogResponseDto result = blogService.getById(blogId);

    // Then
    verify(blogRepository).incrementViews(eq(blogId));
    verify(blogRepository).findById(eq(blogId));
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(blogId);
  }

  @Test
  @DisplayName("should increment views exactly once per getById call")
  void shouldIncrementViewsExactlyOnce() {
    // Given
    Long blogId = 5L;
    Blog blog = new Blog();
    blog.setId(blogId);
    blog.setTitle("Another Blog");
    blog.setContent("Another Content");
    blog.setViews(10L);

    User author = new User();
    author.setId(2L);
    author.setUsername("anotheruser");
    blog.setAuthor(author);

    when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
    when(likeRepository.countByBlog_Id(blogId)).thenReturn(0L);

    // When
    blogService.getById(blogId);

    // Then
    verify(blogRepository).incrementViews(eq(blogId));
  }
}
