package com.huseynovvusal.springblogapi.mapper;

import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.dto.response.UserSummaryDto;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;

/**
 * Utility class for mapping {@link Blog} entities to {@link BlogResponseDto} DTOs.
 */
public final class BlogMapper {

    // Prevent instantiation
    private BlogMapper() {}

    /**
     * Converts a {@link Blog} entity to a {@link BlogResponseDto}.
     *
     * @param blog the blog entity to convert
     * @return the corresponding BlogResponseDto
     */
    public static BlogResponseDto toDto(Blog blog) {
        if (blog == null) return null;

        return new BlogResponseDto(
                blog.getId(),
                blog.getTitle(),
                blog.getContent(),
                blog.getCreatedAt(),
                blog.getUpdatedAt(),
                toUserSummary(blog.getAuthor())
        );
    }

    /**
     * Converts a {@link User} entity to a {@link UserSummaryDto}.
     *
     * @param user the user entity to convert
     * @return the corresponding UserSummaryDto, or null if user is null
     */
    public static UserSummaryDto toUserSummary(User user) {
        if (user == null) return null;

        return new UserSummaryDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
