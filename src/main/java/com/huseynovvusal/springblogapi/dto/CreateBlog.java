package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for creating a new blog post.
 * Contains the title and content provided by the user.
 */
@Data
public class CreateBlog {

    /**
     * Title of the blog post.
     */
    @NotBlank(message = "Title is required")
    private String title;

    /**
     * Content/body of the blog post.
     */
    @NotBlank(message = "Content is required")
    private String content;
}
