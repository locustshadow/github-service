# GitHub Profile Service

A Spring Boot REST service that aggregates GitHub user profile data from multiple GitHub API endpoints into a unified response.

## Tech Stack

- Java 25
- Spring Boot 4.0.1
- Spring RestClient
- Lombok
- springdoc-openapi (Swagger)
- JUnit 6 / Mockito (testing)

## Build & Run

### Command Line

```bash
# Build
./mvnw clean compile

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run
```

### IntelliJ

Open the project and run the `Application` class directly (click the play button in the gutter).

---

The service starts on `http://localhost:8080`

## API

### Get User Profile

Fetches GitHub user info and repositories.

```
GET /api/v1/users/{username}/profile
```

**Response (200 OK):**
```json
{
  "user_name": "octocat",
  "display_name": "The Octocat",
  "avatar": "https://avatars.githubusercontent.com/u/583231",
  "geo_location": "San Francisco",
  "email": "octocat@github.com",
  "url": "https://api.github.com/users/octocat",
  "created_at": "Tue, 25 Jan 2011 18:44:36 GMT",
  "repos": [
    {
      "name": "hello-world",
      "url": "https://github.com/octocat/hello-world"
    }
  ],
  "repo_count": 1
}
```

**Error Responses:**

| Status | Description |
|--------|-------------|
| 404 | User not found on GitHub |
| 502 | GitHub API error |
| 500 | Internal server error |

## API Documentation

Once the application is running:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/v3/api-docs

## Configuration

GitHub API base URL can be configured in `application.yaml`:

```yaml
github:
  api:
    base-url: https://api.github.com
```

## Testing

### Command Line

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=ProfileControllerTest
```

### IntelliJ

Right-click on a test class or method and select "Run", or click the play button in the gutter.

### Classes Under Test

| Class Under Test        | Layer | Mocking Approach |
|-------------------------|-------|------------------|
| `ProfileControllerTest` | Controller | `@WebMvcTest` + `@MockitoBean` |
| `ProfileServiceTest`    | Service | `@ExtendWith(MockitoExtension.class)` + `@Mock` |

## Architecture

```
ProfileController (REST API)
    └── ProfileService (Business Logic)
            └── GitHubClient (GitHub API Integration)
```

The service fetches user info and repositories concurrently using `CompletableFuture`, then aggregates them into a single response.

## Project Structure

```
src/main/java/com/branch/service/github/
├── client/           # External API clients (GitHub client)
├── controller/       # REST controllers
│   └── advice/       # Controller advice (i.e - exception handling)
├── exception/        # Custom exception classes
├── model/
│   ├── dto/          # Response DTOs for this service's API
│   └── github/       # DTOs mapping GitHub API responses
└── service/          # Business logic
```
