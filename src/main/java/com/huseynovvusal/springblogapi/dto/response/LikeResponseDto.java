package com.huseynovvusal.springblogapi.dto.response;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Value;

/**
 * Data Transfer Object representing the like status of a blog post. Always includes the total like
 * count. Optionally includes the list of users who liked the post — only populated when explicitly
 * requested to avoid unnecessary DB queries on every call.
 */
@Value
public class LikeResponseDto {

  /**
   * Total number of likes for the blog post. Derived from COUNT query on the likes table — no full
   * row loading needed.
   */
  long likeCount;

  /**
   * Optional list of users who liked the blog post. Null when not requested (e.g. standard blog
   * listing). Populated only on explicit "get likers" endpoint call. Uses UserSummaryDto to keep
   * payload minimal — only id, username, firstName, lastName.
   */
  @Nullable List<UserSummaryDto> likedUsers;
}
