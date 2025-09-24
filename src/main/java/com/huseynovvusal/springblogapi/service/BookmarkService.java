package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.mapper.BlogMapper;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.Bookmark;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import com.huseynovvusal.springblogapi.repository.BookmarkRepository;
import com.huseynovvusal.springblogapi.security.SecurityUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing blog bookmarks by authenticated users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService implements SecurityAwareService {

    private final BookmarkRepository bookmarkRepository;
    private final BlogRepository blogRepository;
    private final EntityManager entityManager;

    /**
     * Adds a bookmark for the current user to the specified blog.
     * Idempotent: does nothing if already bookmarked.
     *
     * @param blogId the ID of the blog to bookmark
     */
    @Transactional
    @CacheEvict(value = "myBookmarks", key = "#root.target.currentUserId()")
    public void addBookmark(Long blogId) {
        Long userId = currentUserId();

        if (bookmarkRepository.existsByUser_IdAndBlog_Id(userId, blogId)) {
            log.debug("Bookmark already exists for user {} and blog {}", userId, blogId);
            return;
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));

        try {
            User userRef = entityManager.getReference(User.class, userId); // avoids SELECT
            bookmarkRepository.save(Bookmark.builder().user(userRef).blog(blog).build());
            log.info("Bookmark added for user {} and blog {}", userId, blogId);
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition detected while adding bookmark for user {} and blog {}", userId, blogId);
            // Safe to ignore due to unique constraint
        }
    }

    /**
     * Removes a bookmark for the current user from the specified blog.
     *
     * @param blogId the ID of the blog to unbookmark
     */
    @Transactional
    @CacheEvict(value = "myBookmarks", key = "#root.target.currentUserId()")
    public void removeBookmark(Long blogId) {
        Long userId = currentUserId();
        bookmarkRepository.deleteByUser_IdAndBlog_Id(userId, blogId);
        log.info("Bookmark removed for user {} and blog {}", userId, blogId);
    }

    /**
     * Checks if the current user has bookmarked the specified blog.
     *
     * @param blogId the ID of the blog
     * @return true if bookmarked, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long blogId) {
        Long userId = currentUserId();
        return bookmarkRepository.existsByUser_IdAndBlog_Id(userId, blogId);
    }

    /**
     * Toggles the bookmark status for the current user on the specified blog.
     *
     * @param blogId the ID of the blog
     * @return true if now bookmarked, false if unbookmarked
     */
    @Transactional
    @CacheEvict(value = "myBookmarks", key = "#root.target.currentUserId()")
    public boolean toggle(Long blogId) {
        Long userId = currentUserId();

        if (bookmarkRepository.existsByUser_IdAndBlog_Id(userId, blogId)) {
            bookmarkRepository.deleteByUser_IdAndBlog_Id(userId, blogId);
            log.info("Bookmark toggled OFF for user {} and blog {}", userId, blogId);
            return false;
        } else {
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
            User userRef = entityManager.getReference(User.class, userId);
            bookmarkRepository.save(Bookmark.builder().user(userRef).blog(blog).build());
            log.info("Bookmark toggled ON for user {} and blog {}", userId, blogId);
            return true;
        }
    }

    /**
     * Lists all blogs bookmarked by the current user.
     *
     * @param pageable pagination information
     * @return a page of blog response DTOs
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "myBookmarks", key = "#root.target.currentUserId()")
    public Page<BlogResponseDto> listMyBookmarks(Pageable pageable) {
        Long userId = currentUserId();
        return bookmarkRepository.findAllByUser_Id(userId, pageable)
                .map(Bookmark::getBlog)
                .map(BlogMapper::toDto);
    }
}
