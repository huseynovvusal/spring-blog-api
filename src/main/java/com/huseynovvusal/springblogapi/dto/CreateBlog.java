package com.huseynovvusal.springblogapi.dto;

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
    private String title;

    /**
     * Content/body of the blog post.
     */
    private String content;
}
