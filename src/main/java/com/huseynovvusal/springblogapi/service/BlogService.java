package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.mapper.BlogMapper;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserService userService;
    public Page<BlogResponseDto> getAllBlogs(Pageable pageable) {
        return blogRepository.findAll(pageable).map(BlogMapper::toDto);
    }
    public BlogResponseDto getById(Long id) {
        Blog b = blogRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Blog not found"));
        return BlogMapper.toDto(b);
    }
    public Page<BlogResponseDto> getByAuthor(String username, Pageable pageable) {
        User author = userService.getUserByUsername(username);
        return blogRepository.findByAuthor(author, pageable).map(BlogMapper::toDto);
    }
    public BlogResponseDto create(CreateBlog request) {
        User current = userService.getCurrentUser();
        Blog b = new Blog();
        b.setTitle(request.getTitle());
        b.setContent(request.getContent());
        b.setAuthor(current);
        Blog saved = blogRepository.save(b);
        return BlogMapper.toDto(saved);
    }
}
