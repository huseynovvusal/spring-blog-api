# Spring Blog API

Spring Blog API is a RESTful backend service for managing blog posts and user authentication, built with Spring Boot. The project is currently under development and not yet feature-complete.

## Features (Planned)
- User registration and authentication (JWT-based)
- CRUD operations for blog posts
- Role-based access control
- Docker support

## Technologies Used
- Java
- Spring Boot
- Spring Security
- JWT
- Docker
- Gradle

## Getting Started

### Prerequisites
- Java 24
- Gradle
- Docker

### Setup
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
4. (Optional) Run with Docker:
   ```bash
   docker-compose up --build
   ```

### Configuration
Edit `src/main/resources/application.yml` to configure database and other settings.

## API Documentation
API endpoints and documentation will be provided as development progresses.

## Contributing
See [CONTRIBUTION.md](CONTRIBUTION.md) for guidelines.

## License
This project is licensed under the MIT License.

## Status
**This project is in active development and not yet complete. Features and documentation may change.**
