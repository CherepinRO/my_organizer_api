# Super Organizer API

A comprehensive task management REST API built with Kotlin and Spring Boot, featuring Swagger documentation and H2 database integration.

## 🚀 Features

- **Complete CRUD Operations**: Create, Read, Update, and Delete tasks
- **Advanced Search**: Search tasks by multiple criteria
- **Priority Management**: High, Medium, and Low priority levels
- **Task Types**: Work and Home task categorization
- **Deadline Tracking**: Optional deadline management
- **Swagger Documentation**: Interactive API documentation
- **Data Validation**: Input validation with proper error handling
- **Sample Data**: Pre-populated with sample tasks for testing

## 📋 Task Fields

Each task contains the following fields:
- **Date**: Task date (required)
- **Task Name**: Name of the task (required)
- **Comment**: Additional notes (optional)
- **Deadline**: Task deadline (optional)
- **Priority**: HIGH, MEDIUM, or LOW (required)
- **Task Type**: WORK or HOME (required)

## 🛠️ Technology Stack

- **Kotlin**: Primary programming language
- **Spring Boot**: Web framework and dependency injection
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory database for development
- **Swagger/OpenAPI**: API documentation
- **Gradle**: Build tool
- **Jakarta Validation**: Input validation

## 🏃‍♂️ Getting Started

### Prerequisites

- Java 17 or higher
- Gradle (or use the included Gradle wrapper)

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd super-organizer
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080/api/tasks`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console`

## 📚 API Endpoints

### Basic Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/tasks` | Get all tasks |
| POST   | `/api/tasks` | Create a new task |
| GET    | `/api/tasks/{id}` | Get task by ID |
| PUT    | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

### Search and Filter

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/tasks/search` | Search tasks by criteria |
| GET    | `/api/tasks/priority/{priority}` | Get tasks by priority |
| GET    | `/api/tasks/type/{taskType}` | Get tasks by type |
| GET    | `/api/tasks/deadline?hasDeadline={true/false}` | Get tasks with/without deadline |

### Ordering

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/tasks/ordered/deadline` | Get tasks ordered by deadline |
| GET    | `/api/tasks/ordered/priority` | Get tasks ordered by priority |
| GET    | `/api/tasks/ordered/date` | Get tasks ordered by date |

## 📝 Example Usage

### Creating a Task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2024-01-15",
    "taskName": "Complete project report",
    "comment": "Quarterly project summary",
    "deadline": "2024-01-20T17:00:00",
    "priority": "HIGH",
    "taskType": "WORK"
  }'
```

### Searching Tasks

```bash
# Search by task name
curl "http://localhost:8080/api/tasks/search?taskName=project"

# Search by priority
curl "http://localhost:8080/api/tasks/search?priority=HIGH"

# Search by date range
curl "http://localhost:8080/api/tasks/search?startDate=2024-01-01&endDate=2024-01-31"

# Combined search
curl "http://localhost:8080/api/tasks/search?priority=HIGH&taskType=WORK"
```

### Updating a Task

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2024-01-16",
    "taskName": "Updated task name",
    "comment": "Updated comment",
    "deadline": "2024-01-25T15:00:00",
    "priority": "MEDIUM",
    "taskType": "HOME"
  }'
```

## 🗃️ Database Configuration

The application uses H2 in-memory database with the following settings:
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`
- **Console**: Available at `http://localhost:8080/h2-console`

## 🔧 Configuration

Application configuration is in `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

## 🧪 Testing

The application includes sample data for testing purposes. When the application starts, it automatically creates sample tasks if the database is empty.

## 📖 API Documentation

Interactive API documentation is available via Swagger UI at:
`http://localhost:8080/swagger-ui.html`

The documentation includes:
- All available endpoints
- Request/response schemas
- Example requests and responses
- Parameter descriptions
- Error codes and messages

## 🏗️ Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/superorganizer/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # REST controllers
│   │       ├── dto/            # Data Transfer Objects
│   │       ├── model/          # Entity models
│   │       ├── repository/     # Data repositories
│   │       └── service/        # Business logic
│   └── resources/
│       └── application.yml     # Application configuration
└── test/                       # Test classes
```

## 🚀 Deployment

For production deployment:

1. **Configure a persistent database** (PostgreSQL, MySQL, etc.)
2. **Update application.yml** with production database settings
3. **Set production profiles** and environment variables
4. **Build the application** with `./gradlew build`
5. **Run the JAR file** with `java -jar build/libs/super-organizer-1.0.0.jar`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔗 Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Swagger/OpenAPI Documentation](https://swagger.io/docs/)
- [H2 Database Documentation](https://www.h2database.com/html/main.html)