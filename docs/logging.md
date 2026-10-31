# Logging

This document explains how logging is configured in Spring Blog API, where to find logs, and how
to read them when something is failing.

## Where logs go

Logging is configured in `src/main/resources/application.yml` under the `logging:` key. By default
logs are written to two places at once:

- **Console** — shown in `docker compose logs -f api` (or your IDE's run console) when running
  locally.
- **File** — `logs/spring-blog-api.log` inside the application's working directory (the container's
  filesystem when run via Docker Compose).

There is currently **no centralized log dashboard** (e.g. Grafana/Loki, ELK) wired into this
project — logs are plain text in the console and log file described above. If you need a searchable,
aggregated view across runs, the most common approach is to ship the log file (or stdout) to a log
aggregator of your choice; that's out of scope for this doc and not something the project currently
provides out of the box.

### Viewing logs locally

```bash
# Tail the API container's logs
docker compose logs -f api

# Or, if running the app directly with Gradle
./gradlew bootRun
# logs print to the console and are also written to logs/spring-blog-api.log
```

## Log levels used in this codebase

The project uses SLF4J via Lombok's `@Slf4j`. The levels in use, and what they mean here:

| Level | Meaning in this codebase |
| --- | --- |
| `ERROR` | Something failed unexpectedly and needs attention — an unhandled exception, a dependency call that failed (e.g. `EmailService` failing to send an email). |
| `WARN` | An abnormal-but-handled condition — a request rejected because the JWT was invalid, a user lookup that came back empty, the circuit breaker reporting the database as unavailable. |
| `INFO` | A normal, expected lifecycle event worth keeping in production logs — a user registered, logged in, a blog was created/updated/deleted, a like/bookmark was added. |
| `DEBUG` | Fine-grained detail useful while developing or troubleshooting a specific request, but too noisy to keep on by default (e.g. per-request JWT parsing steps). |

By default, Spring Security's own logging is set to `DEBUG` (see `logging.level.org.springframework.security`
in `application.yml`); the application's own loggers follow Spring Boot's default level (`INFO`)
unless you override them.

### Changing the log level

Add or edit `logging.level.<package>` in `application.yml` (or override via the
`LOGGING_LEVEL_<PACKAGE>` environment variable), for example:

```yaml
logging:
  level:
    com.huseynovvusal.springblogapi: DEBUG
```

### Reading failures from the log

The console pattern now includes the log level and logger name:

```
2026-10-31 12:00:00 [WARN] c.h.s.filter.DatabaseHealthFilter - Circuit breaker is in OPEN state - considering database unavailable
```

Previously the console pattern only printed the timestamp and message
(`"%d{yyyy-MM-dd HH:mm:ss} - %msg%n"`), so an `ERROR` line looked identical to an `INFO` line unless
you read the message text carefully. If you're debugging an incident, grep the log file for
`ERROR` and `WARN` first:

```bash
grep -E "\[(ERROR|WARN)\]" logs/spring-blog-api.log
```

### Notable failure-relevant log lines

A few of the log statements most useful when something is going wrong:

- `DatabaseHealthFilter` — logs at `WARN` when the circuit breaker opens and the API starts
  returning `503 Service Unavailable` for all requests. This used to be logged at `DEBUG`, which
  meant a live outage wouldn't show up in logs unless `DEBUG` logging was already enabled — it's
  now `WARN` so it's visible by default.
- `JwtAuthenticationFilter` — logs an unhandled exception during JWT processing at `ERROR`.
- `EmailService` — logs at `ERROR` whenever a welcome/reset email fails to send.
- `AuthenticationService` — logs at `ERROR` when a login or password-reset request references a
  user that can't be found.
