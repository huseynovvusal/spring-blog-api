package com.huseynovvusal.springblogapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Entity representing a user's bookmark of a blog post.
 * Ensures each user can bookmark a blog only once.
 */
@Entity
@Table(
        name = "bookmarks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_blog",
                columnNames = {"user_id", "blog_id"}
        )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bookmark {

    /**
     * Unique identifier for the bookmark entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who created the bookmark.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_bookmark_user")
    )
    private User user;

    /**
     * The blog post that was bookmarked.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "blog_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_bookmark_blog")
    )
    private Blog blog;

    /**
     * Timestamp when the bookmark was created.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
