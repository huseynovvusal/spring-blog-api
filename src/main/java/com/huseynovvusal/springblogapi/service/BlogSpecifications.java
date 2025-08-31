package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.model.Blog;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;

/**
 * Utility class for building dynamic JPA Specifications for Blog filtering.
 */
public final class BlogSpecifications {

    private BlogSpecifications() {}

    /**
     * Filters blogs whose title contains the given query string (case-insensitive).
     *
     * @param q the search query
     * @return specification for title matching
     */
    public static Specification<Blog> titleContains(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            String like = "%" + q.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("title")), like);
        };
    }

    /**
     * Filters blogs that have at least one of the specified tag names.
     *
     * @param tagNames collection of tag names
     * @return specification for tag matching
     */
    public static Specification<Blog> hasAnyTag(Collection<String> tagNames) {
        return (root, query, cb) -> {
            if (tagNames == null || tagNames.isEmpty()) return cb.conjunction();
            var tagsJoin = root.join("tags", JoinType.LEFT);
            query.distinct(true);
            var loweredTags = tagNames.stream().map(String::toLowerCase).toList();
            return cb.lower(tagsJoin.get("name")).in(loweredTags);
        };
    }

    /**
     * Filters blogs authored by a user with the specified username.
     *
     * @param username the author's username
     * @return specification for author matching
     */
    public static Specification<Blog> hasAuthorUsername(String username) {
        return (root, query, cb) -> {
            if (username == null || username.isBlank()) return cb.conjunction();
            var authorJoin = root.join("author", JoinType.LEFT);
            return cb.equal(cb.lower(authorJoin.get("username")), username.toLowerCase());
        };
    }

    /**
     * Filters blogs created within the specified date range.
     *
     * @param from start date (inclusive)
     * @param to   end date (inclusive)
     * @return specification for creation date range
     */
    public static Specification<Blog> createdBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();

            var createdAtPath = root.get("createdAt").as(Date.class);
            Date fromDate = (from != null) ? Date.from(from) : null;
            Date toDate = (to != null) ? Date.from(to) : null;

            if (fromDate != null && toDate != null) {
                return cb.between(createdAtPath, fromDate, toDate);
            } else if (fromDate != null) {
                return cb.greaterThanOrEqualTo(createdAtPath, fromDate);
            } else {
                return cb.lessThanOrEqualTo(createdAtPath, toDate);
            }
        };
    }

    /**
     * Performs a full-text search on both title and content fields.
     *
     * @param q the search query
     * @return specification for text search
     */
    public static Specification<Blog> textSearch(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("content").as(String.class)), like)
            );
        };
    }
}
