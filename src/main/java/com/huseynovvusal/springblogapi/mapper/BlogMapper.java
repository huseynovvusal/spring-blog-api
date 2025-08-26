package com.huseynovvusal.springblogapi.mapper;

import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.dto.response.UserSummaryDto;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;

public final class BlogMapper {
    private BlogMapper() {}

    public static BlogResponseDto toDto(Blog blog) {
        return new BlogResponseDto(
                blog.getId(),
                blog.getTitle(),
                blog.getContent(),
                blog.getCreatedAt(),
                blog.getUpdatedAt(),
                toUserSummary(blog.getAuthor()),
                blog.getTags()
        );
    }

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
