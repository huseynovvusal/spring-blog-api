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

### 🔖 Bookmarks / Favorites

Authenticated users can bookmark their favorite blog posts for quick access.

#### Endpoints
All requests require an `Authorization: Bearer <JWT>` header.

- **Add bookmark** (idempotent)  
  `POST /api/bookmarks/{blogId}`  
  → `204 No Content`

- **Check if bookmarked**  
  `GET /api/bookmarks/check?blogId={blogId}`  
  → `true | false`

- **List my bookmarks** (paged)  
  `GET /api/bookmarks?page=0&size=10&sort=createdAt,desc`  
  → returns a page of `BlogResponseDto`

- **Toggle bookmark**  
  `POST /api/bookmarks/{blogId}/toggle`  
  → `true` (added) or `false` (removed)

- **Remove bookmark** (idempotent)  
  `DELETE /api/bookmarks/{blogId}`  
  → `204 No Content`

#### Notes
- Duplicate prevention is enforced by a unique database constraint `(user_id, blog_id)` and idempotent service logic.
- The current user is resolved from the JWT claim `uid`.
- Error codes:
    - `401 Unauthorized` — missing/expired token
    - `404 Not Found` — blog does not exist


## 🤝 Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License
This project is licensed under the MIT License.

## 🚧 Status
**This project is in active development and not yet complete. Features and documentation may change.**
