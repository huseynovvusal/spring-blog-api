package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.Comment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
    Comment deleteById(Long id);

    Comment save(Comment content);

    Comment findById(Long id);
}
