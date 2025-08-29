package com.huseynovvusal.springblogapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "bookmarks", uniqueConstraints = @UniqueConstraint(name = "uk_user_blog", columnNames = {"user_id","blog_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bookmark_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bookmark_blog"))
    private Blog blog;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant createdAt;
}
