package com.huseynovvusal.springblogapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import com.huseynovvusal.springblogapi.repository.LikeRepository;
import com.huseynovvusal.springblogapi.security.RichTextSanitizer;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlogService Unit Tests")
class BlogServiceTest {

  @Mock private BlogRepository blogRepository;
  @Mock private LikeRepository likeRepository;
  @Mock private UserService userService;
  @Mock private RichTextSanitizer richTextSanitizer;

  private BlogService blogService;

  @BeforeEach
  void setup() {
    blogService = new BlogService(blogRepository, userService, richTextSanitizer, likeRepository);
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

  @Test
  @DisplayName("should throw NoSuchElementException when blog does not exist")
  void shouldThrowWhenBlogNotFound() {
    // Given
    Long blogId = 99L;
    when(blogRepository.findById(blogId)).thenReturn(Optional.empty());

    // When / Then
    assertThrows(NoSuchElementException.class, () -> blogService.getById(blogId));
    verify(blogRepository).incrementViews(eq(blogId));
  }

  @Test
  @DisplayName("should create a blog with sanitized content unchanged")
  void shouldCreateBlogWhenContentUnchanged() {
    // Given
    CreateBlog request = new CreateBlog("Testing Valid Title", "Testing Content");

    User currentUser = new User();
    currentUser.setId(1L);
    currentUser.setUsername("testuser");

    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(richTextSanitizer.sanitize("Testing Content")).thenReturn("Testing Content");
    when(blogRepository.save(any(Blog.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(likeRepository.countByBlog_Id(any())).thenReturn(0L);

    // When
    BlogResponseDto result = blogService.create(request);

    // Then
    assertThat(result.getContent()).isEqualTo("Testing Content");
    assertThat(result.getTitle()).isEqualTo("Testing Valid Title");
    verify(userService).getCurrentUser();
    verify(richTextSanitizer).sanitize("Testing Content");
    verify(blogRepository).save(any(Blog.class));
  }

  @Test
  @DisplayName("should create a blog with sanitized content when sanitizer modifies input")
  void shouldCreateBlogWithSanitizedContent() {
    // Given
    CreateBlog request = new CreateBlog("Testing Valid Title", "<script>Testing Content</script>");

    User currentUser = new User();
    currentUser.setId(1L);
    currentUser.setUsername("testuser");

    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(richTextSanitizer.sanitize("<script>Testing Content</script>"))
        .thenReturn("Testing Content");
    when(blogRepository.save(any(Blog.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(likeRepository.countByBlog_Id(any())).thenReturn(0L);

    // When
    BlogResponseDto result = blogService.create(request);

    // Then
    assertThat(result.getContent()).isEqualTo("Testing Content");
    assertThat(result.getContent()).doesNotContain("<script>");
    verify(richTextSanitizer).sanitize("<script>Testing Content</script>");
    verify(blogRepository).save(any(Blog.class));
  }

  @Test
  @DisplayName("should return blogs by author")
  void shouldReturnBlogsByAuthor() {
    // Given
    String username = "testuser";
    Pageable pageable = PageRequest.of(0, 10);

    User author = new User();
    author.setId(1L);
    author.setUsername(username);

    Blog blog = new Blog();
    blog.setId(1L);
    blog.setTitle("Author's Blog");
    blog.setContent("Some content");
    blog.setAuthor(author);

    Page<Blog> blogPage = new PageImpl<>(List.of(blog), pageable, 1);

    when(userService.getUserByUsername(username)).thenReturn(author);
    when(blogRepository.findByAuthor(author, pageable)).thenReturn(blogPage);
    when(likeRepository.countByBlog_Id(any())).thenReturn(0L);

    // When
    Page<BlogResponseDto> result = blogService.getByAuthor(username, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Author's Blog");
    verify(userService).getUserByUsername(username);
    verify(blogRepository).findByAuthor(author, pageable);
  }

  @Test
  @DisplayName("should return filtered results")
  void shouldReturnFilteredResults() {
    // Given
    List<String> tags = List.of("java");
    String authorUsername = "testuser";
    Instant createdFrom = Instant.EPOCH;
    Instant createdTo = Instant.now();
    String query = "spring";
    Pageable pageable = PageRequest.of(0, 10);

    User author = new User();
    author.setId(1L);
    author.setUsername(authorUsername);

    Blog blog = new Blog();
    blog.setId(1L);
    blog.setTitle("Spring Boot Guide");
    blog.setContent("Some content");
    blog.setAuthor(author);

    Page<Blog> blogPage = new PageImpl<>(List.of(blog), pageable, 1);

    when(blogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(blogPage);
    when(likeRepository.countByBlog_Id(any())).thenReturn(0L);

    // When
    Page<BlogResponseDto> result =
        blogService.filter(tags, authorUsername, createdFrom, createdTo, query, null, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Spring Boot Guide");
    verify(blogRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  @DisplayName("should return search results")
  void shouldReturnSearchResults() {
    // Given
    String query = "spring";
    Pageable pageable = PageRequest.of(0, 10);

    User author = new User();
    author.setId(1L);
    author.setUsername("testuser");

    Blog blog = new Blog();
    blog.setId(1L);
    blog.setTitle("Spring Boot Guide");
    blog.setContent("Some content");
    blog.setAuthor(author);

    Page<Blog> blogPage = new PageImpl<>(List.of(blog), pageable, 1);

    when(blogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(blogPage);
    when(likeRepository.countByBlog_Id(any())).thenReturn(0L);

    // When
    Page<BlogResponseDto> result = blogService.search(query, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Spring Boot Guide");
    verify(blogRepository).findAll(any(Specification.class), eq(pageable));
  }
}
