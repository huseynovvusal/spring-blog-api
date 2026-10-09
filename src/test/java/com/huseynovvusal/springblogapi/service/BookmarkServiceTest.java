package com.huseynovvusal.springblogapi.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huseynovvusal.springblogapi.exception.BlogNotFoundException;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.Bookmark;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import com.huseynovvusal.springblogapi.repository.BookmarkRepository;
import com.huseynovvusal.springblogapi.repository.LikeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

  @Mock private BookmarkRepository bookmarkRepository;

  @Mock private BlogRepository blogRepository;

  @Mock private EntityManager entityManager;

  @Mock private LikeRepository likeRepository;

  private BookmarkService bookmarkService;

  @BeforeEach
  void setup() {
    bookmarkService =
        Mockito.spy(
            new BookmarkService(bookmarkRepository, blogRepository, entityManager, likeRepository));

    doReturn(1L).when(bookmarkService).currentUserId();
  }

  @Test
  void addBookmarkShouldDoNothingWhenBookmarkAlreadyExists() throws BlogNotFoundException {
    // Given
    Long blogId = 10L;

    when(bookmarkRepository.existsByUser_IdAndBlog_Id(1L, blogId)).thenReturn(true);

    // When
    bookmarkService.addBookmark(blogId);

    // Then
    verify(bookmarkRepository, never()).save(any(Bookmark.class));
  }

  @Test
  void addBookmarkShouldThrowExceptionWhenBlogDoesNotExist() {
    // Given
    Long blogId = 10L;

    when(bookmarkRepository.existsByUser_IdAndBlog_Id(1L, blogId)).thenReturn(false);

    when(blogRepository.findById(blogId)).thenReturn(java.util.Optional.empty());

    // When & Then
    assertThrows(BlogNotFoundException.class, () -> bookmarkService.addBookmark(blogId));

    verify(bookmarkRepository, never()).save(any(Bookmark.class));
  }

  @Test
  void removeBookmarkShouldDeleteBookmark() {
    // Given
    Long blogId = 10L;

    // When
    bookmarkService.removeBookmark(blogId);

    // Then
    verify(bookmarkRepository).deleteByUser_IdAndBlog_Id(1L, blogId);
  }

  @Test
  void isBookmarkedShouldReturnRepositoryResult() {
    // Given
    Long blogId = 10L;

    when(bookmarkRepository.existsByUser_IdAndBlog_Id(1L, blogId)).thenReturn(true);

    // When
    boolean result = bookmarkService.isBookmarked(blogId);

    // Then
    assertTrue(result);

    verify(bookmarkRepository).existsByUser_IdAndBlog_Id(1L, blogId);
  }

  @Test
  void toggleShouldAddBookmarkWhenNotAlreadyBookmarked() throws BlogNotFoundException {
    // Given
    Long blogId = 10L;

    Blog blog = new Blog();
    blog.setId(blogId);

    User userRef = new User();
    userRef.setId(1L);

    when(bookmarkRepository.existsByUser_IdAndBlog_Id(1L, blogId)).thenReturn(false);

    when(blogRepository.findById(blogId)).thenReturn(java.util.Optional.of(blog));

    when(entityManager.getReference(User.class, 1L)).thenReturn(userRef);

    // When
    boolean result = bookmarkService.toggle(blogId);

    // Then
    assertTrue(result);

    verify(bookmarkRepository).save(any(Bookmark.class));
  }

  @Test
  void toggleShouldRemoveBookmarkWhenAlreadyBookmarked() throws BlogNotFoundException {
    // Given
    Long blogId = 10L;

    when(bookmarkRepository.existsByUser_IdAndBlog_Id(1L, blogId)).thenReturn(true);

    // When
    boolean result = bookmarkService.toggle(blogId);

    // Then
    assertFalse(result);

    verify(bookmarkRepository).deleteByUser_IdAndBlog_Id(1L, blogId);

    verify(bookmarkRepository, never()).save(any(Bookmark.class));
  }
}
