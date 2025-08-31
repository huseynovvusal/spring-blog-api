package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.mapper.BlogMapper;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import static com.huseynovvusal.springblogapi.service.BlogSpecifications.*;

/**
 * Service class for managing blog-related operations.
 * Handles creation, retrieval, filtering, and author-based queries.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserService userService;

    /**
     * Retrieves all blogs with pagination.
     *
     * @param pageable pagination and sorting information
     * @return a page of blog response DTOs
     */
    public Page<BlogResponseDto> getAllBlogs(Pageable pageable) {
        log.debug("Fetching all blogs with pagination: {}", pageable);
        return blogRepository.findAll(pageable).map(BlogMapper::toDto);
    }

    /**
     * Retrieves a blog by its ID.
     *
     * @param id the blog ID
     * @return the corresponding blog response DTO
     * @throws NoSuchElementException if the blog is not found
     */
    public BlogResponseDto getById(Long id) {
        log.debug("Fetching blog by ID: {}", id);
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Blog not found with ID: {}", id);
                    return new NoSuchElementException("Blog not found");
                });
        return BlogMapper.toDto(blog);
    }

    /**
     * Retrieves blogs authored by a specific user.
     *
     * @param username the author's username
     * @param pageable pagination information
     * @return a page of blog response DTOs
     */
    public Page<BlogResponseDto> getByAuthor(String username, Pageable pageable) {
        log.debug("Fetching blogs by author: {}", username);
        User author = userService.getUserByUsername(username);
        return blogRepository.findByAuthor(author, pageable).map(BlogMapper::toDto);
    }

    /**
     * Creates a new blog post for the currently authenticated user.
     *
     * @param request the blog creation request
     * @return the created blog response DTO
     */
    public BlogResponseDto create(CreateBlog request) {
        User currentUser = userService.getCurrentUser();
        log.info("Creating blog for user: {}", currentUser.getUsername());

        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setAuthor(currentUser);

        Blog saved = blogRepository.save(blog);
        log.debug("Blog created with ID: {}", saved.getId());

        return BlogMapper.toDto(saved);
    }

    /**
     * Filters blogs based on tags, author, creation date, and search query.
     *
     * @param tags          list of tag names
     * @param authorUsername author's username
     * @param createdFrom   start of creation date range
     * @param createdTo     end of creation date range
     * @param q             search query for title
     * @param onlyPublished flag to filter published blogs (not yet implemented)
     * @param pageable      pagination information
     * @return a page of filtered blog response DTOs
     */
    public Page<BlogResponseDto> filter(
            List<String> tags,
            String authorUsername,
            Instant createdFrom,
            Instant createdTo,
            String q,
            Boolean onlyPublished,
            Pageable pageable
    ) {
        log.debug("Filtering blogs with criteria - tags: {}, author: {}, from: {}, to: {}, query: {}",
                tags, authorUsername, createdFrom, createdTo, q);

        Specification<Blog> spec = Specification.allOf(
                hasAuthorUsername(authorUsername),
                createdBetween(createdFrom, createdTo),
                titleContains(q),
                hasAnyTag(tags)
        );

        return blogRepository.findAll(spec, pageable).map(BlogMapper::toDto);
    }
}
