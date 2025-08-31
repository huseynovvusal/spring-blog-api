package com.huseynovvusal.springblogapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a blog post.
 * Includes metadata, author reference, tags, and view count.
 */
@Entity
@Table(name = "blogs")
@Getter
@Setter
public class Blog {

    /**
     * Unique identifier for the blog post.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the blog post.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Main content of the blog post.
     */
    @Lob
    @Column(nullable = false)
    private String content;

    /**
     * Author of the blog post.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    /**
     * Timestamp when the blog was created.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    private Date createdAt;

    /**
     * Timestamp when the blog was last updated.
     */
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    /**
     * Tags associated with the blog post.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "blog_tags",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    /**
     * Number of times the blog has been viewed.
     */
    @Column(nullable = false)
    private long views = 0L;
}
