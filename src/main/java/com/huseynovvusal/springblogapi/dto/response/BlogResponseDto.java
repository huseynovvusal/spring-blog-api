package com.huseynovvusal.springblogapi.dto.response;

import lombok.Value;
import java.util.Date;

/**
 * Data Transfer Object representing a blog post response.
 * Contains metadata and author information for client consumption.
 */
@Value
public class BlogResponseDto {

    /**
     * Unique identifier of the blog post.
     */
    Long id;

    /**
     * Title of the blog post.
     */
    String title;

    /**
     * Content/body of the blog post.
     */
    String content;

    /**
     * Timestamp when the blog was created.
     */
    Date createdAt;

    /**
     * Timestamp when the blog was last updated.
     */
    Date updatedAt;

    /**
     * Summary information about the author of the blog.
     */
    UserSummaryDto author;
}
