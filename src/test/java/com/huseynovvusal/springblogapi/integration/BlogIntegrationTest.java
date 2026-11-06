package com.huseynovvusal.springblogapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.dto.LoginRequest;
import com.huseynovvusal.springblogapi.dto.RegisterRequest;
import com.huseynovvusal.springblogapi.repository.BlogRepository;
import com.huseynovvusal.springblogapi.repository.RefreshTokenRepository;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import com.huseynovvusal.springblogapi.service.EmailService;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests covering the blog-post workflow end to end: authenticated creation, retrieval,
 * update, deletion, listing/search, and the error responses returned for invalid input, missing
 * resources, and unauthorized access. Exercises the real Spring context, security filter chain, and
 * an in-memory H2 database (see {@code src/test/resources/application.yml}).
 */
@SpringBootTest(
    properties = {
      "server.servlet.context-path=/api/v1",
      "resilience4j.ratelimiter.configs.auth.limit-for-period=100",
      "resilience4j.ratelimiter.configs.default.limit-for-period=100"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlogIntegrationTest {

  private static final String CONTEXT_PATH = "/api/v1";
  private static final String REGISTER_PATH = CONTEXT_PATH + "/auth/register";
  private static final String LOGIN_PATH = CONTEXT_PATH + "/auth/login";
  private static final String BLOGS_PATH = CONTEXT_PATH + "/blogs";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Autowired private BlogRepository blogRepository;

  @Autowired private CacheManager cacheManager;

  @MockitoBean private EmailService emailService;

  @BeforeEach
  void setUp() {
    blogRepository.deleteAll();
    refreshTokenRepository.deleteAll();
    userRepository.deleteAll();
    cacheManager
        .getCacheNames()
        .forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
  }

  @Test
  @DisplayName("Should complete the full blog workflow: create, read, update, list, delete")
  void shouldCompleteBlogWorkflow() throws Exception {
    String token = registerAndLogin("alice", "alice@example.com");

    JsonNode created =
        createBlog(token, new CreateBlog("My First Blog Post", "Hello, this is the content."));
    long blogId = created.get("id").asLong();
    assertThat(created.get("title").asText()).isEqualTo("My First Blog Post");
    assertThat(created.get("views").asLong()).isZero();

    JsonNode fetched = getBlog(token, blogId);
    assertThat(fetched.get("id").asLong()).isEqualTo(blogId);
    // getById increments the view count as a side effect.
    assertThat(fetched.get("views").asLong()).isEqualTo(1);

    JsonNode updated =
        updateBlog(token, blogId, new CreateBlog("My Updated Blog Post", "Updated content here."));
    assertThat(updated.get("title").asText()).isEqualTo("My Updated Blog Post");
    assertThat(updated.get("content").asText()).isEqualTo("Updated content here.");

    mockMvc
        .perform(get(BLOGS_PATH).contextPath(CONTEXT_PATH).header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(blogId));

    mockMvc
        .perform(
            delete(BLOGS_PATH + "/" + blogId)
                .contextPath(CONTEXT_PATH)
                .header("Authorization", bearer(token)))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            get(BLOGS_PATH + "/" + blogId)
                .contextPath(CONTEXT_PATH)
                .header("Authorization", bearer(token)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should find a created blog through search and filter")
  void shouldFindBlogThroughSearchAndFilter() throws Exception {
    String token = registerAndLogin("bob", "bob@example.com");
    createBlog(token, new CreateBlog("Spring Boot Testing Guide", "Covers MockMvc and more."));

    mockMvc
        .perform(
            get(BLOGS_PATH + "/search")
                .param("q", "Testing")
                .contextPath(CONTEXT_PATH)
                .header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("Spring Boot Testing Guide"));

    mockMvc
        .perform(
            get(BLOGS_PATH + "/filter")
                .param("author", "bob")
                .contextPath(CONTEXT_PATH)
                .header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("Spring Boot Testing Guide"));
  }

  @Test
  @DisplayName("Should reject blog access without an Authorization header")
  void shouldRejectBlogAccessWithoutAuthentication() throws Exception {
    mockMvc.perform(get(BLOGS_PATH).contextPath(CONTEXT_PATH)).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 404 when fetching a blog that does not exist")
  void shouldReturnNotFoundForMissingBlog() throws Exception {
    String token = registerAndLogin("carol", "carol@example.com");

    mockMvc
        .perform(
            get(BLOGS_PATH + "/999999")
                .contextPath(CONTEXT_PATH)
                .header("Authorization", bearer(token)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Blog not found"));
  }

  @Test
  @DisplayName("Should reject blog creation when the title is too short")
  void shouldRejectCreationWithInvalidTitle() throws Exception {
    String token = registerAndLogin("dave", "dave@example.com");

    mockMvc
        .perform(
            post(BLOGS_PATH)
                .contextPath(CONTEXT_PATH)
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new CreateBlog("Hi", "Some valid content."))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(
            jsonPath("$.fieldErrors.title").value("Title must be between 5 and 150 characters"));
  }

  @Test
  @DisplayName("Should reject updating another user's blog post")
  void shouldRejectUpdatingAnotherUsersBlog() throws Exception {
    String ownerToken = registerAndLogin("erin", "erin@example.com");
    JsonNode created =
        createBlog(ownerToken, new CreateBlog("Erin's Blog Post", "Original content here."));
    long blogId = created.get("id").asLong();

    String otherToken = registerAndLogin("frank", "frank@example.com");

    mockMvc
        .perform(
            put(BLOGS_PATH + "/" + blogId)
                .contextPath(CONTEXT_PATH)
                .header("Authorization", bearer(otherToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new CreateBlog("Hijacked Title", "Hijacked content here."))))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  @DisplayName("Should reject deleting another user's blog post")
  void shouldRejectDeletingAnotherUsersBlog() throws Exception {
    String ownerToken = registerAndLogin("gina", "gina@example.com");
    JsonNode created =
        createBlog(ownerToken, new CreateBlog("Gina's Blog Post", "Original content here."));
    long blogId = created.get("id").asLong();

    String otherToken = registerAndLogin("harry", "harry@example.com");

    mockMvc
        .perform(
            delete(BLOGS_PATH + "/" + blogId)
                .contextPath(CONTEXT_PATH)
                .header("Authorization", bearer(otherToken)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  private String registerAndLogin(String username, String email) throws Exception {
    RegisterRequest registerRequest =
        new RegisterRequest("Test", "User", username, email, "Password123!");

    mockMvc
        .perform(
            post(REGISTER_PATH)
                .contextPath(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk());

    String response =
        mockMvc
            .perform(
                post(LOGIN_PATH)
                    .contextPath(CONTEXT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new LoginRequest(username, "Password123!"))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response).get("token").asText();
  }

  private JsonNode createBlog(String token, CreateBlog body) throws Exception {
    String response =
        mockMvc
            .perform(
                post(BLOGS_PATH)
                    .contextPath(CONTEXT_PATH)
                    .header("Authorization", bearer(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response);
  }

  private JsonNode getBlog(String token, long id) throws Exception {
    String response =
        mockMvc
            .perform(
                get(BLOGS_PATH + "/" + id)
                    .contextPath(CONTEXT_PATH)
                    .header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response);
  }

  private JsonNode updateBlog(String token, long id, CreateBlog body) throws Exception {
    String response =
        mockMvc
            .perform(
                put(BLOGS_PATH + "/" + id)
                    .contextPath(CONTEXT_PATH)
                    .header("Authorization", bearer(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response);
  }

  private String bearer(String token) {
    return "Bearer " + token;
  }
}
