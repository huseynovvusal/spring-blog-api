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

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/emoji-key/)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/huseynovvusal"><img src="https://avatars.githubusercontent.com/u/87518350?s=100" width="100px;" alt="huseynovvusal"/><br /><sub><b>huseynovvusal</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=huseynovvusal" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/jaindeepak1401"><img src="https://avatars.githubusercontent.com/u/93066547?s=100" width="100px;" alt="jaindeepak1401"/><br /><sub><b>jaindeepak1401</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=jaindeepak1401" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Surya00011"><img src="https://avatars.githubusercontent.com/u/131737876?s=100" width="100px;" alt="Surya00011"/><br /><sub><b>Surya00011</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=Surya00011" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/LuisMunizEnc"><img src="https://avatars.githubusercontent.com/u/210091604?s=100" width="100px;" alt="LuisMunizEnc"/><br /><sub><b>LuisMunizEnc</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=LuisMunizEnc" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/c-arianna"><img src="https://avatars.githubusercontent.com/u/152692185?s=100" width="100px;" alt="c-arianna"/><br /><sub><b>c-arianna</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=c-arianna" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/axgiri"><img src="https://avatars.githubusercontent.com/u/146159445?s=100" width="100px;" alt="axgiri"/><br /><sub><b>axgiri</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=axgiri" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/chandraKiranBolla"><img src="https://avatars.githubusercontent.com/u/186732099?s=100" width="100px;" alt="chandraKiranBolla"/><br /><sub><b>chandraKiranBolla</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=chandraKiranBolla" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/SRokesh-28"><img src="https://avatars.githubusercontent.com/u/176596613?s=100" width="100px;" alt="SRokesh-28"/><br /><sub><b>SRokesh-28</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=SRokesh-28" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Pranjall-Gupta"><img src="https://avatars.githubusercontent.com/u/174760024?s=100" width="100px;" alt="Pranjall-Gupta"/><br /><sub><b>Pranjall-Gupta</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=Pranjall-Gupta" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/SantiagoLuna109"><img src="https://avatars.githubusercontent.com/u/203914119?s=100" width="100px;" alt="SantiagoLuna109"/><br /><sub><b>SantiagoLuna109</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=SantiagoLuna109" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/xinjia-ctrl"><img src="https://avatars.githubusercontent.com/u/188528424?s=100" width="100px;" alt="xinjia-ctrl"/><br /><sub><b>xinjia-ctrl</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=xinjia-ctrl" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/shivaprasadphshiva"><img src="https://avatars.githubusercontent.com/u/219318111?s=100" width="100px;" alt="shivaprasadphshiva"/><br /><sub><b>shivaprasadphshiva</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=shivaprasadphshiva" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/naookko"><img src="https://avatars.githubusercontent.com/u/114715116?s=100" width="100px;" alt="naookko"/><br /><sub><b>naookko</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=naookko" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/liuyuanyuan090"><img src="https://avatars.githubusercontent.com/u/106043703?s=100" width="100px;" alt="liuyuanyuan090"/><br /><sub><b>liuyuanyuan090</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=liuyuanyuan090" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/joonseolee"><img src="https://avatars.githubusercontent.com/u/43516757?s=100" width="100px;" alt="joonseolee"/><br /><sub><b>joonseolee</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=joonseolee" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/jkim1202"><img src="https://avatars.githubusercontent.com/u/112814261?s=100" width="100px;" alt="jkim1202"/><br /><sub><b>jkim1202</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=jkim1202" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/fercamoub"><img src="https://avatars.githubusercontent.com/u/209853419?s=100" width="100px;" alt="fercamoub"/><br /><sub><b>fercamoub</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=fercamoub" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Loumo-on"><img src="https://avatars.githubusercontent.com/u/149517773?s=100" width="100px;" alt="Loumo-on"/><br /><sub><b>Loumo-on</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=Loumo-on" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gp07-cmd"><img src="https://avatars.githubusercontent.com/u/263711587?v=4?s=100" width="100px;" alt="gp07-cmd"/><br /><sub><b>gp07-cmd</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=gp07-cmd" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/NimishNatani"><img src="https://avatars.githubusercontent.com/u/135497893?v=4?s=100" width="100px;" alt="NimishNatani"/><br /><sub><b>NimishNatani</b></sub></a><br /><a href="https://github.com/huseynovvusal/spring-blog-api/commits?author=NimishNatani" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

This project is licensed under the MIT License.

## 🚧 Status

**This project is in active development and not yet complete. Features and documentation may change.**
