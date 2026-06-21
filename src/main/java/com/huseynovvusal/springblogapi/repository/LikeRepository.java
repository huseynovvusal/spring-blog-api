package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.Likes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Like} entities. Provides methods for checking existence,
 * counting, deletion, and retrieval. Spring Data JPA derives all queries from method names — no
 * manual SQL needed.
 */
public interface LikeRepository extends JpaRepository<Likes, Long> {

  /**
   * Checks if a like exists for the given user and blog. Used in service layer to prevent duplicate
   * likes (happy path check). Spring Data translates this to: SELECT COUNT(*) > 0 WHERE user_id=?
   * AND blog_id=?
   */
  boolean existsByUser_IdAndBlog_Id(Long userId, Long blogId);

  /**
   * Deletes a like by user ID and blog ID. Used for unlike operation — removes the row directly
   * without loading the entity. Returns number of rows deleted (0 if not found, 1 if deleted).
   */
  long deleteByUser_IdAndBlog_Id(Long userId, Long blogId);

  /**
   * Counts total likes for a given blog. Used to return like count in blog responses. Spring Data
   * translates this to: SELECT COUNT(*) WHERE blog_id=?
   */
  long countByBlog_Id(Long blogId);

  /**
   * Retrieves all likes for a given blog with pagination. Used for the optional "list of users who
   * liked this post" feature. Spring Data translates this to: SELECT * WHERE blog_id=? LIMIT ?
   * OFFSET ?
   */
  Page<Likes> findAllByBlog_Id(Long blogId, Pageable pageable);
}
