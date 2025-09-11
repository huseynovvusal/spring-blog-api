package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.service.BlogService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;

/**
 * Controller for managing blog-related operations.
 * Supports CRUD operations and filtering based on tags, author, and creation date.
 */
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);
    private final BlogService blogService;

    /**
     * Retrieves a paginated list of all blogs, sorted by creation date descending.
     *
     * @param pageable pagination and sorting information
     * @return paginated list of blog responses
     */
    @GetMapping
    public ResponseEntity<Page<BlogResponseDto>> getAllBlogs(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        logger.info("Fetching all blogs with pagination: {}", pageable);
        return ResponseEntity.ok(blogService.getAllBlogs(pageable));
    }

    /**
     * Retrieves a blog by its unique ID.
     *
     * @param id the blog ID
     * @return the blog response
     */
    @GetMapping("/{id}")
    @RateLimiter(name = "default")
    public ResponseEntity<BlogResponseDto> getById(@PathVariable Long id) {
        logger.info("Fetching blog with ID: {}", id);
        return ResponseEntity.ok(blogService.getById(id));
    }

    /**
     * Retrieves blogs authored by a specific user.
     *
     * @param username the author's username
     * @param pageable pagination and sorting information
     * @return paginated list of blog responses
     */
    @GetMapping("/author/{username}")
    public ResponseEntity<Page<BlogResponseDto>> getByAuthor(
            @PathVariable String username,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        logger.info("Fetching blogs by author: {} with pagination: {}", username, pageable);
        return ResponseEntity.ok(blogService.getByAuthor(username, pageable));
    }

    /**
     * Creates a new blog post.
     *
     * @param body the blog creation request
     * @return the created blog response
     */
    @PostMapping
    public ResponseEntity<BlogResponseDto> create(@Valid @RequestBody CreateBlog body) {
        logger.info("Creating new blog with title: {}", body.getTitle());
        return ResponseEntity.ok(blogService.create(body));
    }

    /**
     * Filters blogs based on tags, author, creation date range, search query, and publication status.
     *
     * @param tags          list of tags to filter by
     * @param author        author's username
     * @param createdFrom   start of creation date range
     * @param createdTo     end of creation date range
     * @param q             search query
     * @param onlyPublished whether to include only published blogs
     * @param pageable      pagination information
     * @return paginated list of filtered blog responses
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<BlogResponseDto>> filter(
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyPublished,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        logger.info("Filtering blogs with tags: {}, author: {}, createdFrom: {}, createdTo: {}, query: {}, onlyPublished: {}",
                tags, author, createdFrom, createdTo, q, onlyPublished);
        return ResponseEntity.ok(blogService.filter(tags, author, createdFrom, createdTo, q, onlyPublished, pageable));
    }
}
