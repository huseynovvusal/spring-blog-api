package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.service.BlogService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing blog-related operations. Supports CRUD operations and filtering based on
 * tags, author, and creation date.
 */
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BlogController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BlogController.class);
  private final BlogService blogService;

  /**
   * Retrieves a paginated list of all blogs, sorted by creation date descending.
   *
   * @param pageable pagination and sorting information
   * @return paginated list of blog responses
   */
  @Operation(
      summary = "List blog posts",
      description = "Retrieves a paginated list of all blogs sorted by creation date.")
  @GetMapping
  public Page<BlogResponseDto> getAllBlogs(
      @PageableDefault(
              size = 20,
              sort = "createdAt",
              direction = org.springframework.data.domain.Sort.Direction.DESC)
          Pageable pageable) {
    LOGGER.info("Fetching all blogs with pagination: {}", pageable);
    return blogService.getAllBlogs(pageable);
  }

  /**
   * Retrieves a blog by its unique ID.
   *
   * @param id the blog ID
   * @return the blog response
   */
  @Operation(summary = "Get blog post", description = "Retrieves a blog by its unique ID.")
  @GetMapping("/{id}")
  @RateLimiter(name = "default")
  public BlogResponseDto getById(@PathVariable Long id) {
    LOGGER.info("Fetching blog with ID: {}", id);
    return blogService.getById(id);
  }

  /**
   * Retrieves blogs authored by a specific user.
   *
   * @param username the author's username
   * @param pageable pagination and sorting information
   * @return paginated list of blog responses
   */
  @Operation(
      summary = "List blogs by author",
      description = "Retrieves paginated blogs authored by a specific user.")
  @GetMapping("/author/{username}")
  public Page<BlogResponseDto> getByAuthor(
      @PathVariable String username,
      @PageableDefault(
              size = 20,
              sort = "createdAt",
              direction = org.springframework.data.domain.Sort.Direction.DESC)
          Pageable pageable) {
    LOGGER.info("Fetching blogs by author: {} with pagination: {}", username, pageable);
    return blogService.getByAuthor(username, pageable);
  }

  /**
   * Creates a new blog post.
   *
   * @param body the blog creation request
   * @return the created blog response
   */
  @Operation(summary = "Create blog post", description = "Creates a new blog post.")
  @PostMapping
  public BlogResponseDto create(@Valid @RequestBody CreateBlog body) {
    LOGGER.info("Creating new blog with title: {}", body.getTitle());
    return blogService.create(body);
  }

  /**
   * Filters blogs based on tags, author, creation date range, search query, and publication status.
   *
   * @param tags list of tags to filter by
   * @param author author's username
   * @param createdFrom start of creation date range
   * @param createdTo end of creation date range
   * @param q search query
   * @param onlyPublished whether to include only published blogs
   * @param pageable pagination information
   * @return paginated list of filtered blog responses
   */
  @Operation(
      summary = "Filter blog posts",
      description = "Filters blogs by tags, author, creation date, query, and publication status.")
  @GetMapping("/filter")
  public Page<BlogResponseDto> filter(
      @RequestParam(required = false) List<String> tags,
      @RequestParam(required = false) String author,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant createdFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant createdTo,
      @RequestParam(required = false) String q,
      @RequestParam(required = false, defaultValue = "false") Boolean onlyPublished,
      @PageableDefault(size = 20) Pageable pageable) {
    LOGGER.info(
        "Filtering blogs with tags: {}, author: {}, createdFrom: {}, createdTo: {}, query: {}, onlyPublished: {}",
        tags,
        author,
        createdFrom,
        createdTo,
        q,
        onlyPublished);
    return blogService.filter(tags, author, createdFrom, createdTo, q, onlyPublished, pageable);
  }

  /**
   * Searches blogs by keyword in title, content, or tags.
   *
   * @param q search keyword
   * @param pageable pagination information
   * @return paginated list of matching blogs
   */
  @Operation(summary = "Search blog posts", description = "Search blogs by title, content or tags")
  @GetMapping("/search")
  public Page<BlogResponseDto> search(
      @RequestParam String q, @PageableDefault(size = 20) Pageable pageable) {

    LOGGER.info("Searching blogs with keyword: {}", q);
    return blogService.search(q, pageable);
  }
}
