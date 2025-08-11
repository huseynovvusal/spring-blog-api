package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final UserService userService;
    private final BlogRepository blogRepository;

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElse(null);
    }

    public List<Blog> getBlogsByAuthor(String username) {
        User author = userService.getUserByUsername(username);

        return blogRepository.findByAuthor(author);
    }

    public Blog createBlog(CreateBlog createBlog) {
        User currentUser = userService.getCurrentUser();

        // Log the current user's username for debugging purposes
        System.out.println("Current User: " + currentUser.getUsername());

        Blog blog = new Blog();

        blog.setTitle(createBlog.getTitle());
        blog.setContent(createBlog.getContent());
        blog.setAuthor(currentUser);

        Blog createdBlog = blogRepository.save(blog);

        System.out.println("Blog created with ID: " + createdBlog.getId() +
                ", Title: " + createdBlog.getTitle());

        return createdBlog;
    }
}
