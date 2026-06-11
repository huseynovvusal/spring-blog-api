# 🤝 Contribution Guidelines

Thank you for your interest in contributing to **Spring Blog API**! We welcome contributions from developers of all experience levels. Whether you're new to open source or a seasoned pro, your ideas and code are valued here.

## 🟢 Getting Started for Newcomers
1. **Fork the repository** on GitHub.
2. **Clone your fork locally:**
   ```bash
   git clone https://github.com/<your-username>/spring-blog-api.git
   cd spring-blog-api
   ```
3. **Create a new branch for your feature or fix:**
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Make your changes and commit:**
   ```bash
   git add .
   git commit -m "Describe your change"
   ```
5. **Push your branch to your fork:**
   ```bash
   git push origin feature/your-feature-name
   ```
6. **Open a Pull Request** from your branch to the `main` branch of this repository.

## 🧑‍💻 Code Style & Best Practices
- Use Java best practices and follow the conventions used in the project.
- Write meaningful commit messages.
- Add tests for new features or bug fixes.

### 🎨 Formatting & Linting
This project enforces a consistent code style in CI using **Spotless** (with
[google-java-format](https://github.com/google/google-java-format)) and **Checkstyle**.
These run as separate checks on every pull request, so please run them locally
**before** opening a PR to avoid red CI.

**Auto-format your code** (fixes formatting for you):

```bash
./gradlew spotlessApply
```

**Verify formatting** without changing files (this is exactly what CI runs):

```bash
./gradlew spotlessCheck
```

**Find linting issues** with Checkstyle:

```bash
./gradlew checkstyleMain checkstyleTest
```

Or run everything CI checks at once — build, tests, formatting, and linting:

```bash
./gradlew check
```

> 💡 **Tip:** If `spotlessCheck` fails, just run `./gradlew spotlessApply` and it
> will fix the formatting automatically. Checkstyle issues (e.g. wildcard imports
> or missing braces) usually need a manual fix — open the report at
> `build/reports/checkstyle/main.html` to see exactly what to change.


## ✅ Running Tests
Run the full test suite before opening a pull request:

```bash
./gradlew test
```

Generate the JaCoCo coverage report with:

```bash
./gradlew test jacocoTestReport
```

The HTML coverage report is generated at:

```text
build/reports/jacoco/test/html/index.html
```

Integration tests should exercise realistic API workflows through Spring Boot Test, including successful flows and expected error responses.

## 🐞 Issue Reporting
- Search for existing issues before opening a new one.
- Provide clear steps to reproduce, expected behavior, and screenshots/logs if applicable.
- Tag your issue appropriately (e.g., `bug`, `feature`, `question`).

## 🗣️ Communication & Support
- If you need help, open an issue or start a discussion.
- Be respectful and constructive in all interactions.

## 🚧 Development Status
- The project is not yet complete. Some features and APIs may change.
- Major refactoring or new features should be discussed in an issue before starting work.

---
Thank you for helping improve Spring Blog API! We look forward to your contributions.
