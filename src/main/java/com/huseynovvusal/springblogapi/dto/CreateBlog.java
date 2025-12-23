package com.huseynovvusal.springblogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new blog post.
 * Contains the title and content provided by the user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBlog {

    /**
     * Title of the blog post.
     */
    @NotBlank(message = "Title is required")
    @Size(min=5,max=150,message="Title must be between 5 and 150 characters")
    private String title;

    /**
     * Content/body of the blog post.
     */
    @NotBlank(message = "Content is required")
    private String content;
}
