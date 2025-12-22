# Notification Service (Backend)

Modular backend implementing a notification system with Clean Architecture, SOLID, and resilience. Uses Flyway for migrations and PostgreSQL in Docker.

## API Endpoints

### POST /api/notifications

Send a notification to all users subscribed to the specified category.

**Request Body:**

```json
{
  "category": "Sports",
  "message": "Your team won the championship!"
}
```

**Response:** 202 Accepted (empty body)

**Valid Categories:** Sports, Finance, Movies

### GET /api/notifications/log

Retrieve notification history.

**Response:** 200 OK

```json
[
  {
    "id": 1,
    "type": "Notification",
    "userName": "Alice Johnson",
    "category": "Sports",
    "channel": "SMS",
    "message": "Your team won the championship!",
    "timestamp": "2025-12-22T03:15:30Z"
  }
]
```

**Example cURL:**

```bash
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"category":"Sports","message":"Your team won the championship!"}'
```

## Key Features

- **Clean Architecture**
  - Domain: Entities and business rules (User, Category, Channel, Subscription, NotificationLog)
  - Application: Use cases and services (NotificationService)
  - Infrastructure: Persistence (Spring Data JPA), configuration, adapters
- **Design Patterns**
  - Strategy Pattern to send notifications via different channels (SMS, E-Mail, Push)
- **Resilience**
  - Fault tolerance and error isolation in the service layer
- **Database**
  - PostgreSQL with Flyway migrations (`V1__Create_Schema.sql`, `V2__Seed_Data.sql`)

## Tech Stack

- Java 21, Spring Boot 3, Spring Data JPA, Flyway, Lombok, Validation
- PostgreSQL (Docker)
- Build: Maven

## Running Locally

- Prerequisites: JDK 21, Maven, PostgreSQL (or use docker-compose in repo root)
- Start:
  ```bash
  mvn spring-boot:run
  ```
- Config: src/main/resources/application.properties

## Testing and Coverage

- **Unit & Integration**: JUnit 5, Mockito, Spring Boot Test, H2
- **Coverage**: JaCoCo (>85% target)
  - Generate report:
    ```bash
    mvn test jacoco:report
    ```
  - HTML report:
    - backend/target/site/jacoco/index.html

## Code Style

- **Google Java Format** via Spotless
- CI automatically formats and commits code changes when possible
- Manual formatting:
  ```bash
  mvn spotless:apply
  ```

## Project Notes

- **Strategy implementations**:
  - EmailStrategy, SmsStrategy, PushNotificationStrategy
- **Repositories**: Spring Data JPA for User, Category, Channel, Subscriptions, UserChannels, NotificationLog
- **Clean separation** of concerns across Domain, Application, and Infrastructure layers
