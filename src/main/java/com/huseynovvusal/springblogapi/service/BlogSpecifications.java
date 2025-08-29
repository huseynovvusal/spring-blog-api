package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.model.Blog;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;

public final class  BlogSpecifications {
    private BlogSpecifications() {}

    public static Specification<Blog> titleContains(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            String like = "%" + q.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("title")), like);
        };
    }

    public static Specification<Blog> hasAnyTag(Collection<String> tagNames) {
        return (root, query, cb) -> {
            if (tagNames == null || tagNames.isEmpty()) return cb.conjunction();
            var tagsJoin = root.join("tags", JoinType.LEFT); // <-- field must match Blog.tags
            query.distinct(true);
            var lowered = tagNames.stream().map(String::toLowerCase).toList();
            return cb.lower(tagsJoin.get("name").as(String.class)).in(lowered);
        };
    }

    public static Specification<Blog> hasAuthorUsername(String username){
        return (root, query, cb) -> {
            if (username == null || username.isBlank()) return cb.conjunction();
            var author = root.join("author", JoinType.LEFT);
            return cb.equal(cb.lower(author.get("username")), username.toLowerCase());
        };
    }

    public static Specification<Blog> createdBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();

            var path = root.<Date>get("createdAt"); // or: root.get("createdAt").as(Date.class)

            Date fromDate = (from != null) ? Date.from(from) : null;
            Date toDate   = (to   != null) ? Date.from(to)   : null;

            if (fromDate != null && toDate != null) {
                return cb.between(path, fromDate, toDate);
            } else if (fromDate != null) {
                return cb.greaterThanOrEqualTo(path, fromDate);
            } else {
                return cb.lessThanOrEqualTo(path, toDate);
            }
        };
    }


    public static Specification<Blog> textSearch(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            var like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("content").as(String.class)), like) // if content is large/LOB
            );
        };
    }
}
