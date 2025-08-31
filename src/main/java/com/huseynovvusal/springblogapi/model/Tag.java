package com.huseynovvusal.springblogapi.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a tag used to categorize blog posts.
 * Each tag must have a unique name.
 */
@Entity
@Table(
        name = "tags",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    /**
     * Unique identifier for the tag.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the tag. Must be unique and no longer than 64 characters.
     */
    @Column(nullable = false, length = 64)
    private String name;
}
