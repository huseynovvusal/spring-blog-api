package com.huseynovvusal.springblogapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entity representing a user's bookmark of a blog post. Ensures each user can bookmark a blog only
 * once.
 */
@Entity
@Table(
    name = "bookmarks",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_user_blog",
            columnNames = {"user_id", "blog_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bookmark {

  /** Unique identifier for the bookmark entry. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The user who created the bookmark. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_bookmark_user"))
  private User user;

  /** The blog post that was bookmarked. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "blog_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_bookmark_blog"))
  private Blog blog;

  /** Timestamp when the bookmark was created. */
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;
}
