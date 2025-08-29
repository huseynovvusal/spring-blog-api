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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BlogRepository blogRepository;
    private final EntityManager entityManager;

    @Transactional
    public void addBookmark(Long blogId) {
        Long userId = SecurityUtils.currentUserId();

        if (bookmarkRepository.existsByUser_IdAndBlog_Id(userId, blogId)) {
            return; // idempotent
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));

        try {
            User userRef = entityManager.getReference(User.class, userId); // no SELECT
            bookmarkRepository.save(Bookmark.builder().user(userRef).blog(blog).build());
        } catch (DataIntegrityViolationException ignoreOnRace) {
            // unique (user_id, blog_id) guarantees no duplicates if concurrent
        }
    }

    @Transactional
    public void removeBookmark(Long blogId) {
        Long userId = SecurityUtils.currentUserId();
        bookmarkRepository.deleteByUser_IdAndBlog_Id(userId, blogId);
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(Long blogId) {
        Long userId = SecurityUtils.currentUserId();
        return bookmarkRepository.existsByUser_IdAndBlog_Id(userId, blogId);
    }

    @Transactional
    public boolean toggle(Long blogId) {
        Long userId = SecurityUtils.currentUserId();
        if (bookmarkRepository.existsByUser_IdAndBlog_Id(userId, blogId)) {
            bookmarkRepository.deleteByUser_IdAndBlog_Id(userId, blogId);
            return false; // now unbookmarked
        } else {
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
            User userRef = entityManager.getReference(User.class, userId);
            bookmarkRepository.save(Bookmark.builder().user(userRef).blog(blog).build());
            return true; // now bookmarked
        }
    }

    @Transactional(readOnly = true)
    public Page<BlogResponseDto> listMyBookmarks(Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        // If you also want newest first by default, ensure an index on (user_id, created_at DESC)
        return bookmarkRepository.findAllByUser_Id(userId, pageable)
                .map(Bookmark::getBlog)
                .map(BlogMapper::toDto);
    }
}
