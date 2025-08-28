package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findByAuthor(User author, Pageable pageable);

    @Query("select distinct b from Blog b left join b.tags t " +
            "where (:q is null or lower(b.title) like lower(concat('%', :q, '%')) " +
            "or lower(b.content) like lower(concat('%', :q, '%'))) " +
            "and (:tag is null or lower(t) = lower(:tag))")
    Page<Blog> search(
            @Param("q") String query,
            @Param("tag") String tag,
            Pageable pageable);
}
