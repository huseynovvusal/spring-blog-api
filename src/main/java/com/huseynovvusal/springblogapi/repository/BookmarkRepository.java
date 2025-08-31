package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.Bookmark;
import com.huseynovvusal.springblogapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Bookmark} entities.
 * Provides methods for checking existence, retrieval, deletion, and pagination.
 */
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

        /**
         * Checks if a bookmark exists for the given user and blog.
         */
        boolean existsByUser_IdAndBlog_Id(Long userId, Long blogId);

        /**
         * Finds a bookmark by user ID and blog ID.
         */
        Optional<Bookmark> findByUser_IdAndBlog_Id(Long userId, Long blogId);

        /**
         * Deletes a bookmark by user ID and blog ID.
         *
         * @return number of rows affected
         */
        long deleteByUser_IdAndBlog_Id(Long userId, Long blogId);

        /**
         * Retrieves all bookmarks for a given user with pagination.
         */
        Page<Bookmark> findAllByUser(User user, Pageable pageable);

        /**
         * Retrieves all bookmarks by user ID with pagination.
         */
        Page<Bookmark> findAllByUser_Id(Long userId, Pageable pageable);
}
