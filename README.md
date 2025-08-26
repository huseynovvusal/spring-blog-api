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
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```
4. Run Docker:
   ```bash
   docker-compose up
   ```

### ⚙️ Configuration

Edit `src/main/resources/application.yml` to configure database and other settings.

## 📖 API Documentation

### Search API

Search blog posts by keyword in title/content and by tag. Results are paginated.

Endpoint:

```
GET /api/blogs/search?q=<keyword>&tag=<tag>&page=<n>&size=<n>&sort=createdAt,desc
```

Query params:

- `q` (optional): keyword to match in title or content (case-insensitive)
- `tag` (optional): exact tag match (case-insensitive)
- `page`, `size`, `sort`: standard Spring pagination params

Example:

```
GET /api/blogs/search?q=spring&tag=java&size=10
```

Create blog with tags:

```
POST /api/blogs
{
  "title": "Intro to Spring",
  "content": "...",
  "tags": ["spring", "java"]
}
```

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

This project is licensed under the MIT License.

## 🚧 Status

**This project is in active development and not yet complete. Features and documentation may change.**
