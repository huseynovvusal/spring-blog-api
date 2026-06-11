package com.huseynovvusal.springblogapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huseynovvusal.springblogapi.dto.LoginRequest;
import com.huseynovvusal.springblogapi.dto.RefreshTokenRequest;
import com.huseynovvusal.springblogapi.dto.RegisterRequest;
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

@SpringBootTest(
    properties = {
      "server.servlet.context-path=/api/v1",
      "resilience4j.ratelimiter.configs.auth.limit-for-period=100",
      "resilience4j.ratelimiter.configs.default.limit-for-period=100"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationIntegrationTest {

  private static final String CONTEXT_PATH = "/api/v1";
  private static final String REGISTER_PATH = CONTEXT_PATH + "/auth/register";
  private static final String LOGIN_PATH = CONTEXT_PATH + "/auth/login";
  private static final String REFRESH_PATH = CONTEXT_PATH + "/auth/refresh";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Autowired private CacheManager cacheManager;

  @MockitoBean private EmailService emailService;

  @BeforeEach
  void setUp() {
    refreshTokenRepository.deleteAll();
    userRepository.deleteAll();
    cacheManager
        .getCacheNames()
        .forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
  }

  @Test
  @DisplayName(
      "Should complete the authentication workflow from registration to refresh token rotation")
  void shouldCompleteAuthenticationWorkflow() throws Exception {
    JsonNode registration = register(registerRequest("alice", "alice@example.com"));
    assertThat(registration.get("token").asText()).isNotBlank();
    assertThat(registration.get("refreshToken").asText()).isNotBlank();

    JsonNode firstLogin = login("alice", "Password123!");
    assertThat(firstLogin.get("token").asText()).isNotBlank();
    assertThat(firstLogin.get("refreshToken").asText()).isNotBlank();

    JsonNode secondLogin = login("alice", "Password123!");
    assertThat(secondLogin.get("token").asText()).isNotBlank();
    assertThat(secondLogin.get("refreshToken").asText()).isNotBlank();

    String originalRefreshToken = secondLogin.get("refreshToken").asText();
    JsonNode refreshedTokens = refresh(originalRefreshToken);
    String rotatedRefreshToken = refreshedTokens.get("refreshToken").asText();

    assertThat(refreshedTokens.get("token").asText()).isNotBlank();
    assertThat(rotatedRefreshToken).isNotBlank();
    assertThat(rotatedRefreshToken).isNotEqualTo(originalRefreshToken);

    refreshShouldFail(originalRefreshToken);

    JsonNode secondRefresh = refresh(rotatedRefreshToken);
    assertThat(secondRefresh.get("token").asText()).isNotBlank();
    assertThat(secondRefresh.get("refreshToken").asText()).isNotBlank();
    assertThat(secondRefresh.get("refreshToken").asText()).isNotEqualTo(rotatedRefreshToken);
  }

  @Test
  @DisplayName("Should reject registration when username already exists")
  void registerShouldRejectDuplicateUsername() throws Exception {
    RegisterRequest request = registerRequest("alice", "alice@example.com");
    register(request);

    mockMvc
        .perform(
            post(REGISTER_PATH)
                .contextPath(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("User with username alice already exists"));
  }

  @Test
  @DisplayName("Should reject login when password is invalid")
  void loginShouldRejectInvalidPassword() throws Exception {
    register(registerRequest("alice", "alice@example.com"));

    mockMvc
        .perform(
            post(LOGIN_PATH)
                .contextPath(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest("alice", "wrong-password"))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Authentication failed"));
  }

  @Test
  @DisplayName("Should reject refresh when token format is invalid")
  void refreshShouldRejectInvalidTokenFormat() throws Exception {
    mockMvc
        .perform(
            post(REFRESH_PATH)
                .contextPath(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new RefreshTokenRequest("invalid-refresh-token"))))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Unexpected error"));
  }

  private JsonNode register(RegisterRequest request) throws Exception {
    String response =
        mockMvc
            .perform(
                post(REGISTER_PATH)
                    .contextPath(CONTEXT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response);
  }

  private JsonNode login(String username, String password) throws Exception {
    String response =
        mockMvc
            .perform(
                post(LOGIN_PATH)
                    .contextPath(CONTEXT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest(username, password))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response);
  }

  private void refreshShouldFail(String refreshToken) throws Exception {
    mockMvc
        .perform(
            post(REFRESH_PATH)
                .contextPath(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Refresh token " + refreshToken + " not valid"));
  }

  private JsonNode refresh(String refreshToken) throws Exception {
    String response =
        mockMvc
            .perform(
                post(REFRESH_PATH)
                    .contextPath(CONTEXT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response);
  }

  private RegisterRequest registerRequest(String username, String email) {
    return new RegisterRequest("Alice", "Tester", username, email, "Password123!");
  }

  private LoginRequest loginRequest(String username, String password) {
    return new LoginRequest(username, password);
  }
}
