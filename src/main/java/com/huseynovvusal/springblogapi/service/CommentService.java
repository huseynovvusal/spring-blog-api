package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.CommentRequest;
import com.huseynovvusal.springblogapi.model.Comment;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.CommentRepository;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public Comment addComments(Comment content){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username);

        content.setUser(user);
        return commentRepository.save(content);
    }

    public List<Comment> listComments(Pageable pageable){
        return commentRepository.findAll(pageable)
                .stream()
                .map(c -> new Comment(c.getId(), c.getContent(), c.getUser().getUsername()))
                .collect(Collectors.toList());
    }

    public Comment deleteComments(Long id){
        return commentRepository.deleteById(id);
    }

    public Comment updateComments(Long id, CommentRequest dto){
        Comment comment = commentRepository.findById(id);

        if(dto.getContent() != null){
            comment.setContent(dto.getContent());
        }

        return commentRepository.save(comment);
    }
}
