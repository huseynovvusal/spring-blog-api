# 🚀 Spring Blog API

Welcome to **Spring Blog API**! This is a modern, secure, and scalable RESTful backend service for managing blog posts and user authentication, built with Spring Boot. The project is currently under active development and new features are coming soon.

> **🌱 Newcomers Welcome!**
> We encourage developers of all experience levels to contribute. Whether you're new to open source or a seasoned pro, your ideas and code are valued here. Check out our [CONTRIBUTING.md](CONTRIBUTING.md) for easy ways to get started!

## 🛠️ Technologies Used

- Java 24
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- Hibernate ORM
- PostgreSQL
- JWT (JSON Web Token)
- Lombok
- Spring Mail
- Gradle
- Docker & Docker Compose
- JUnit 5
- PgAdmin
- Resilience4j (Rate Limiting & Circuit Breaker)

## ⚡ Getting Started

### Prerequisites

- Java 24
- Docker (optional, for containerization)
- Gradle

### 🚦 Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd spring-blog-api
   ```
2. Build the project:
   ```bash
   ./gradlew build
   ```
3. Run Docker:
   ```bash
   docker-compose up
   ```
4. Use the API service on:
   ```
   http://localhost:8082/api/v1/
   ```

### ⚙️ Configuration

Edit `src/main/resources/application.yml` to configure database and other settings.

## 📖 API Documentation

Interactive API documentation is available via Swagger UI.  
**Most requests require an `Authorization: Bearer <JWT>` header.**

**`http://localhost:8082/api/v1/swagger-ui/index.html#/`**

#### Notes

- Duplicate prevention is enforced by a unique database constraint `(user_id, blog_id)` and idempotent service logic.
- The current user is resolved from the JWT claim `uid`.
- Error codes:
  - `401 Unauthorized` — missing/expired token
  - `404 Not Found` — blog does not exist

## 🛡️ Rate Limiting & Circuit Breaker

This API implements robust rate limiting and circuit breaker patterns to protect against abuse and service failures:

### Rate Limiting

- **Default API endpoints**: 20 requests/60 seconds
- **Authentication endpoints**: 5 requests/60 seconds

### Circuit Breaker

- Automatic database failure detection
- Service degradation during outages
- Graceful error responses (503 Service Unavailable)

## Star History

<a href="https://www.star-history.com/#huseynovvusal/spring-blog-api&Date">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=huseynovvusal/spring-blog-api&type=Date&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=huseynovvusal/spring-blog-api&type=Date" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=huseynovvusal/spring-blog-api&type=Date" />
 </picture>
</a>

## ✨ Contributors

<a href="https://github.com/huseynovvusal/spring-blog-api/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=huseynovvusal/spring-blog-api" />
</a>

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

This project is licensed under the MIT License.

## 🚧 Status

**This project is in active development and not yet complete. Features and documentation may change.**
