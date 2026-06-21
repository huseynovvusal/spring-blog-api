package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.response.LikeResponseDto;
import com.huseynovvusal.springblogapi.dto.response.UserSummaryDto;
import com.huseynovvusal.springblogapi.exception.BlogNotFoundException;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.Likes;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import com.huseynovvusal.springblogapi.repository.LikeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing blog likes by authenticated users. */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService implements SecurityAwareService {

  private final LikeRepository likeRepository;
  private final BlogRepository blogRepository;

  /**
   * EntityManager used to get a proxy reference to User by ID. Avoids an extra SELECT — only the FK
   * value is needed when inserting a Like row.
   */
  private final EntityManager entityManager;

  /**
   * Adds a like for the current user on the specified blog. Idempotent — does nothing if the user
   * already liked the post. Double-guarded: service-level existsBy check + DB unique constraint
   * catch for race conditions.
   *
   * @param blogId the ID of the blog to like
   * @throws BlogNotFoundException if the blog does not exist
   */
  @Transactional
  public void addLike(Long blogId) throws BlogNotFoundException {
    Long userId = currentUserId();

    // Happy path guard — avoids hitting DB constraint on normal duplicate attempts
    if (likeRepository.existsByUser_IdAndBlog_Id(userId, blogId)) {
      log.debug("Like already exists for user {} and blog {}", userId, blogId);
      return;
    }

    // Verify blog exists before inserting
    Blog blog =
        blogRepository
            .findById(blogId)
            .orElseThrow(
                () -> new BlogNotFoundException(String.format("Blog not found: %d", blogId)));

    try {
      // getReference avoids SELECT on users table — only FK value needed for insert
      User userRef = entityManager.getReference(User.class, userId);
      likeRepository.save(Likes.builder().user(userRef).blog(blog).build());
      log.info("Like added for user {} on blog {}", userId, blogId);
    } catch (DataIntegrityViolationException e) {
      // Two requests from the same user arrived simultaneously, both passed existsBy check
      // DB unique constraint uk_user_blog_like caught the second insert — safe to ignore
      log.warn("Race condition detected while adding like for user {} on blog {}", userId, blogId);
    }
  }

  /**
   * Removes a like for the current user from the specified blog. No-op if the user has not liked
   * the post.
   *
   * @param blogId the ID of the blog to unlike
   */
  @Transactional
  public void removeLike(Long blogId) {
    Long userId = currentUserId();
    likeRepository.deleteByUser_IdAndBlog_Id(userId, blogId);
    log.info("Like removed for user {} on blog {}", userId, blogId);
  }

  /**
   * Toggles the like status for the current user on the specified blog. If already liked — removes
   * the like and returns false. If not liked — adds the like and returns true.
   *
   * @param blogId the ID of the blog to toggle like on
   * @return true if now liked, false if unliked
   * @throws BlogNotFoundException if the blog does not exist
   */
  @Transactional
  public boolean toggle(Long blogId) throws BlogNotFoundException {
    Long userId = currentUserId();

    if (likeRepository.existsByUser_IdAndBlog_Id(userId, blogId)) {
      // Already liked — remove it
      likeRepository.deleteByUser_IdAndBlog_Id(userId, blogId);
      log.info("Like toggled OFF for user {} on blog {}", userId, blogId);
      return false;
    } else {
      // Not liked yet — add it
      Blog blog =
          blogRepository
              .findById(blogId)
              .orElseThrow(
                  () -> new BlogNotFoundException(String.format("Blog not found: %d", blogId)));
      User userRef = entityManager.getReference(User.class, userId);
      likeRepository.save(Likes.builder().user(userRef).blog(blog).build());
      log.info("Like toggled ON for user {} on blog {}", userId, blogId);
      return true;
    }
  }

  /**
   * Checks if the current user has liked the specified blog. readOnly = true — Hibernate skips
   * dirty checking, minor performance gain.
   *
   * @param blogId the ID of the blog to check
   * @return true if liked by current user, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean isLiked(Long blogId) {
    Long userId = currentUserId();
    return likeRepository.existsByUser_IdAndBlog_Id(userId, blogId);
  }

  /**
   * Returns the total like count for the specified blog. readOnly = true — only a COUNT query, no
   * entity loading needed.
   *
   * @param blogId the ID of the blog
   * @return total number of likes
   */
  @Transactional(readOnly = true)
  public long getLikeCount(Long blogId) {
    return likeRepository.countByBlog_Id(blogId);
  }

  /**
   * Returns like count and the paginated list of users who liked the specified blog. likedUsers is
   * populated here — only called on explicit request, not on every blog fetch. Each Like row's user
   * is mapped to UserSummaryDto to keep the payload minimal.
   *
   * @param blogId the ID of the blog
   * @param pageable pagination info for the likedUsers list
   * @return LikeResponseDto with likeCount and likedUsers list
   */
  @Transactional(readOnly = true)
  public LikeResponseDto getLikes(Long blogId, Pageable pageable) {
    long likeCount = likeRepository.countByBlog_Id(blogId);

    // Map each Like row's user to UserSummaryDto — minimal payload, no email exposed
    Page<UserSummaryDto> likedUsers =
        likeRepository
            .findAllByBlog_Id(blogId, pageable)
            .map(Likes::getUser)
            .map(
                u ->
                    new UserSummaryDto(
                        u.getId(), u.getUsername(), u.getFirstName(), u.getLastName()));

    return new LikeResponseDto(likeCount, likedUsers.getContent());
  }
}
