package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.response.LikeResponseDto;
import com.huseynovvusal.springblogapi.exception.BlogNotFoundException;
import com.huseynovvusal.springblogapi.service.LikeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing blog likes. Supports adding, removing, toggling, checking, and listing
 * likes. All endpoints require a valid JWT token — enforced by SecurityConfig's
 * anyRequest().authenticated().
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

  private static final Logger LOGGER = LoggerFactory.getLogger(LikeController.class);

  private final LikeService likeService;

  /**
   * Likes the specified blog for the current user. Idempotent — calling multiple times has no extra
   * effect.
   *
   * @param blogId the ID of the blog to like
   * @throws BlogNotFoundException if the blog does not exist
   */
  @PostMapping("/{blogId}")
  public void add(@PathVariable Long blogId) throws BlogNotFoundException {
    LOGGER.info("Adding like for blog ID: {}", blogId);
    likeService.addLike(blogId);
  }

  /**
   * Unlikes the specified blog for the current user. No-op if the user has not liked the post.
   *
   * @param blogId the ID of the blog to unlike
   */
  @DeleteMapping("/{blogId}")
  public void remove(@PathVariable Long blogId) {
    LOGGER.info("Removing like for blog ID: {}", blogId);
    likeService.removeLike(blogId);
  }

  /**
   * Toggles like status for the current user on the specified blog. Returns true if now liked,
   * false if unliked.
   *
   * @param blogId the ID of the blog to toggle
   * @return true if liked, false if unliked
   * @throws BlogNotFoundException if the blog does not exist
   */
  @PostMapping("/{blogId}/toggle")
  public boolean toggle(@PathVariable Long blogId) throws BlogNotFoundException {
    LOGGER.info("Toggling like for blog ID: {}", blogId);
    boolean result = likeService.toggle(blogId);
    LOGGER.debug("Toggle result for blog ID {}: {}", blogId, result);
    return result;
  }

  /**
   * Checks if the current user has already liked the specified blog. Used by frontend to render
   * like button state correctly.
   *
   * @param blogId the ID of the blog to check
   * @return true if liked by current user, false otherwise
   */
  @GetMapping("/check")
  public boolean isLiked(@RequestParam Long blogId) {
    LOGGER.info("Checking like status for blog ID: {}", blogId);
    boolean result = likeService.isLiked(blogId);
    LOGGER.debug("Like status for blog ID {}: {}", blogId, result);
    return result;
  }

  /**
   * Returns the like count and paginated list of users who liked the specified blog. likedUsers
   * list is paginated — prevents unbounded response on popular posts.
   *
   * @param blogId the ID of the blog
   * @param page page number (default 0)
   * @param size page size (default 20)
   * @return LikeResponseDto with likeCount and likedUsers
   */
  @GetMapping("/{blogId}")
  public LikeResponseDto getLikes(
      @PathVariable Long blogId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    LOGGER.info("Getting likes for blog ID: {}", blogId);
    return likeService.getLikes(blogId, PageRequest.of(page, size));
  }
}
