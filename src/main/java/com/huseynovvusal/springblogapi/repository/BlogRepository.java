package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Repository interface for accessing {@link Blog} entities.
 * Supports pagination, dynamic filtering, and custom queries.
 */
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {

    /**
     * Finds blogs authored by a specific user with pagination.
     *
     * @param author   the user who authored the blogs
     * @param pageable pagination information
     * @return a page of blogs authored by the given user
     */
    Page<Blog> findByAuthor(User author, Pageable pageable);

    /**
     * Eagerly fetches the author when retrieving a blog by ID.
     * Useful for avoiding lazy loading issues in service or controller layers.
     */
    @EntityGraph(attributePaths = {"author"})
    Blog findWithAuthorById(Long id);
}
