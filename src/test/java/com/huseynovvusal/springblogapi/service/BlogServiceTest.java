package com.huseynovvusal.springblogapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import com.huseynovvusal.springblogapi.repository.BookmarkRepository;
import com.huseynovvusal.springblogapi.repository.LikeRepository;
import com.huseynovvusal.springblogapi.security.RichTextSanitizer;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlogService Unit Tests")
class BlogServiceTest {

  @Mock private BlogRepository blogRepository;
  @Mock private LikeRepository likeRepository;
  @Mock private BookmarkRepository bookmarkRepository;
  @Mock private UserService userService;
  @Mock private RichTextSanitizer richTextSanitizer;

  private BlogService blogService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    blogService =
        new BlogService(
            blogRepository, userService, richTextSanitizer, likeRepository, bookmarkRepository);
  }

  @Test
  @DisplayName("should evict blog caches on mutation")
  void shouldEvictBlogCachesForMutations() throws Exception {
    Method createMethod = BlogService.class.getDeclaredMethod("create", CreateBlog.class);
    Method updateMethod =
        BlogService.class.getDeclaredMethod("update", Long.class, CreateBlog.class);
    Method deleteMethod = BlogService.class.getDeclaredMethod("delete", Long.class);

    assertThat(createMethod.getAnnotation(CacheEvict.class).value()).contains("blogs");
    assertThat(updateMethod.getAnnotation(CacheEvict.class).value()).contains("filteredBlogs");
    assertThat(deleteMethod.getAnnotation(CacheEvict.class).value()).contains("searchBlogs");
    assertThat(createMethod.getAnnotation(CacheEvict.class).allEntries()).isTrue();
    assertThat(updateMethod.getAnnotation(CacheEvict.class).allEntries()).isTrue();
    assertThat(deleteMethod.getAnnotation(CacheEvict.class).allEntries()).isTrue();
  }

  @Test
  @DisplayName("should list all blogs with like counts")
  void shouldListAllBlogs() {
    Pageable pageable = PageRequest.of(0, 10);
    Blog blog = blog(1L, "Hello", "World", user(2L, "alice"));
    Page<Blog> page = new PageImpl<>(List.of(blog), pageable, 1);

    when(blogRepository.findAll(pageable)).thenReturn(page);
    when(likeRepository.countByBlog_Id(1L)).thenReturn(7L);

    Page<BlogResponseDto> result = blogService.getAllBlogs(pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Hello");
    assertThat(result.getContent().getFirst().getLikeCount()).isEqualTo(7L);
  }

  @Test
  @DisplayName("should increment views and return blog by ID")
  void shouldGetBlogById() {
    Long blogId = 1L;
    Blog blog = blog(blogId, "Test Blog", "Test Content", user(1L, "testuser"));

    when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
    when(likeRepository.countByBlog_Id(blogId)).thenReturn(3L);

    BlogResponseDto result = blogService.getById(blogId);

    verify(blogRepository).incrementViews(eq(blogId));
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(blogId);
    assertThat(result.getLikeCount()).isEqualTo(3L);
  }

  @Test
  @DisplayName("should throw when blog is not found by ID")
  void shouldThrowWhenGetByIdNotFound() {
    Long blogId = 99L;
    when(blogRepository.findById(blogId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> blogService.getById(blogId))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("Blog not found");

    verify(blogRepository).incrementViews(eq(blogId));
  }

  @Test
  @DisplayName("should get blogs by author")
  void shouldGetBlogsByAuthor() {
    String username = "alice";
    User author = user(5L, username);
    Pageable pageable = PageRequest.of(0, 5);
    Blog blog = blog(9L, "Authored", "content", author);
    Page<Blog> page = new PageImpl<>(List.of(blog), pageable, 1);

    when(userService.getUserByUsername(username)).thenReturn(author);
    when(blogRepository.findByAuthor(author, pageable)).thenReturn(page);
    when(likeRepository.countByBlog_Id(9L)).thenReturn(1L);

    Page<BlogResponseDto> result = blogService.getByAuthor(username, pageable);

    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().getFirst().getAuthor().getUsername()).isEqualTo(username);
  }

  @Test
  @DisplayName("should create a blog and sanitize content")
  void shouldCreateBlog() {
    User currentUser = user(7L, "owner");
    String unsafe = "<script>alert('x')</script><b>safe</b>";
    String sanitized = "<b>safe</b>";
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(richTextSanitizer.sanitize(unsafe)).thenReturn(sanitized);
    when(blogRepository.save(any(Blog.class)))
        .thenAnswer(
            invocation -> {
              Blog blog = invocation.getArgument(0);
              blog.setId(11L);
              return blog;
            });
    when(likeRepository.countByBlog_Id(11L)).thenReturn(2L);

    BlogResponseDto result = blogService.create(new CreateBlog("New title", unsafe));

    assertThat(result.getTitle()).isEqualTo("New title");
    assertThat(result.getContent()).isEqualTo(sanitized);
    assertThat(result.getAuthor().getUsername()).isEqualTo("owner");
    assertThat(result.getLikeCount()).isEqualTo(2L);
  }

  @Test
  @DisplayName("should create a blog without sanitizing when content is already safe")
  void shouldCreateBlogWithoutSanitizationChange() {
    User currentUser = user(7L, "owner");
    String content = "<b>safe</b>";
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(richTextSanitizer.sanitize(content)).thenReturn(content);
    when(blogRepository.save(any(Blog.class)))
        .thenAnswer(
            invocation -> {
              Blog blog = invocation.getArgument(0);
              blog.setId(12L);
              return blog;
            });
    when(likeRepository.countByBlog_Id(12L)).thenReturn(0L);

    BlogResponseDto result = blogService.create(new CreateBlog("Plain title", content));

    assertThat(result.getContent()).isEqualTo(content);
    assertThat(result.getLikeCount()).isZero();
  }

  @Test
  @DisplayName("should update an existing blog owned by current user")
  void shouldUpdateBlogOwnedByCurrentUser() {
    User author = user(7L, "owner");
    Blog blog = blog(11L, "Old title", "Old content", author);
    when(userService.getCurrentUser()).thenReturn(author);
    when(blogRepository.findById(11L)).thenReturn(Optional.of(blog));
    when(richTextSanitizer.sanitize("<i>new</i>")).thenReturn("<i>new</i>");
    when(blogRepository.save(blog)).thenReturn(blog);
    when(likeRepository.countByBlog_Id(11L)).thenReturn(4L);

    BlogResponseDto result = blogService.update(11L, new CreateBlog("Updated title", "<i>new</i>"));

    assertThat(result.getTitle()).isEqualTo("Updated title");
    assertThat(result.getContent()).isEqualTo("<i>new</i>");
    assertThat(blog.getTitle()).isEqualTo("Updated title");
  }

  @Test
  @DisplayName("should throw when updating a blog that does not exist")
  void shouldThrowWhenUpdateBlogNotFound() {
    User currentUser = user(7L, "owner");
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(blogRepository.findById(98L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> blogService.update(98L, new CreateBlog("Title", "Content")))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("Blog not found");
  }

  @Test
  @DisplayName("should reject updating another user's blog")
  void shouldRejectUpdateFromAnotherUser() {
    User currentUser = user(7L, "other");
    User author = user(8L, "owner");
    Blog blog = blog(12L, "Title", "Content", author);
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(blogRepository.findById(12L)).thenReturn(Optional.of(blog));

    assertThatThrownBy(() -> blogService.update(12L, new CreateBlog("New", "Edited")))
        .isInstanceOf(AccessDeniedException.class)
        .hasMessage("You can only update your own blog posts");
  }

  @Test
  @DisplayName("should delete a blog and dependent records")
  void shouldDeleteBlogAndDependentRecords() {
    User author = user(9L, "owner");
    Blog blog = blog(21L, "Delete me", "Remove this", author);
    when(userService.getCurrentUser()).thenReturn(author);
    when(blogRepository.findById(21L)).thenReturn(Optional.of(blog));
    when(likeRepository.deleteByBlog_Id(21L)).thenReturn(3L);
    when(bookmarkRepository.deleteByBlog_Id(21L)).thenReturn(2L);

    blogService.delete(21L);

    verify(likeRepository).deleteByBlog_Id(21L);
    verify(bookmarkRepository).deleteByBlog_Id(21L);
    verify(blogRepository).delete(blog);
  }

  @Test
  @DisplayName("should throw when deleting a blog that does not exist")
  void shouldThrowWhenDeleteBlogNotFound() {
    User currentUser = user(9L, "owner");
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(blogRepository.findById(77L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> blogService.delete(77L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("Blog not found");
  }

  @Test
  @DisplayName("should reject deleting another user's blog")
  void shouldRejectDeleteFromAnotherUser() {
    User currentUser = user(9L, "other");
    User author = user(10L, "owner");
    Blog blog = blog(22L, "Delete", "Nope", author);
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(blogRepository.findById(22L)).thenReturn(Optional.of(blog));

    assertThatThrownBy(() -> blogService.delete(22L))
        .isInstanceOf(AccessDeniedException.class)
        .hasMessage("You can only delete your own blog posts");
  }

  @Test
  @DisplayName("should filter blogs")
  void shouldFilterBlogs() {
    Pageable pageable = PageRequest.of(0, 4);
    Blog blog = blog(31L, "Filtered", "content", user(2L, "alice"));
    Page<Blog> page = new PageImpl<>(List.of(blog), pageable, 1);

    when(blogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    when(likeRepository.countByBlog_Id(31L)).thenReturn(8L);

    Page<BlogResponseDto> result =
        blogService.filter(
            List.of("java"), "alice", Instant.now(), Instant.now(), "Filtered", false, pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Filtered");
  }

  @Test
  @DisplayName("should search blogs")
  void shouldSearchBlogs() {
    Pageable pageable = PageRequest.of(0, 3);
    Blog blog = blog(41L, "Search result", "winter", user(2L, "alice"));
    Page<Blog> page = new PageImpl<>(List.of(blog), pageable, 1);

    when(blogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    when(likeRepository.countByBlog_Id(41L)).thenReturn(5L);

    Page<BlogResponseDto> result = blogService.search("winter", pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Search result");
  }

  private User user(Long id, String username) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    return user;
  }

  private Blog blog(Long id, String title, String content, User author) {
    Blog blog = new Blog();
    blog.setId(id);
    blog.setTitle(title);
    blog.setContent(content);
    blog.setAuthor(author);
    return blog;
  }
}
