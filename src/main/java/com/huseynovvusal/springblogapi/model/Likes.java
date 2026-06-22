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
 * Entity representing a user's like on a blog post. Ensures each user can like a blog only once via
 * unique constraint.
 */
@Entity
@Table(
    name = "likes", // 'like' is a reserved SQL keyword, so plural form is used
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_user_blog_like", // named constraint for easier debugging in DB logs
            columnNames = {"user_id", "blog_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Likes {

  /** Unique identifier for the like entry. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user who liked the post. LAZY fetch avoids loading the full User object unless explicitly
   * needed.
   */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_like_user")) // named FK for schema clarity
  private User user;

  /**
   * The blog post that was liked. LAZY fetch avoids loading the full Blog object unless explicitly
   * needed.
   */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "blog_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_like_blog")) // named FK for schema clarity
  private Blog blog;

  /**
   * Timestamp when the like was created. Automatically set by Hibernate on insert, never updated.
   * Useful for time-based queries like "most liked this week".
   */
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;
}
