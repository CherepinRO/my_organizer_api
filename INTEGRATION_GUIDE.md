# Super Organizer - Database Integration Guide

This project integrates the Super Organizer API with both H2 (for development) and PostgreSQL (for production) databases.

## 🚀 Quick Start

### Option 1: Development with H2 Database (Default)

The easiest way to get started is with the H2 in-memory database:

```bash
./gradlew bootRun
```

- **API**: http://localhost:8080/api/tasks
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

### Option 2: Production with PostgreSQL Database

For production-like setup with PostgreSQL:

1. **Start PostgreSQL with Docker**:
   ```bash
   ./setup_postgres.sh
   ```

2. **Run the application with PostgreSQL profile**:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=postgres'
   ```

- **API**: http://localhost:8080/api/tasks
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **pgAdmin**: http://localhost:8080 (admin@superorganizer.com / admin)

## 🗄️ Database Configuration

### H2 Database (Development)
- **Type**: In-memory database
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`
- **Features**: Auto-creates tables, sample data loaded on startup

### PostgreSQL Database (Production)
- **Type**: Persistent database
- **URL**: `jdbc:postgresql://localhost:5432/super_organizer_db`
- **Username**: `postgres`
- **Password**: `password`
- **Features**: Persistent storage, optimized indexes, triggers

## 📋 Database Schema

The application uses the following table structure:

```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    comment TEXT,
    deadline TIMESTAMP,
    priority priority_enum NOT NULL,  -- 'HIGH', 'MEDIUM', 'LOW'
    task_type task_type_enum NOT NULL, -- 'WORK', 'HOME'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 🛠️ API Endpoints

### Basic CRUD Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/tasks` | Get all tasks |
| POST   | `/api/tasks` | Create a new task |
| GET    | `/api/tasks/{id}` | Get task by ID |
| PUT    | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

### Advanced Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/tasks/search` | Search tasks by criteria |
| GET    | `/api/tasks/priority/{priority}` | Get tasks by priority |
| GET    | `/api/tasks/type/{taskType}` | Get tasks by type |
| GET    | `/api/tasks/deadline?hasDeadline={true/false}` | Get tasks with/without deadline |

## 🔧 Configuration Files

### application.yml (H2 Development)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

### application-postgres.yml (PostgreSQL Production)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/super_organizer_db
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: password
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: none
```

## 📝 Sample Usage

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
# Search by priority
curl "http://localhost:8080/api/tasks/search?priority=HIGH"

# Search by task name
curl "http://localhost:8080/api/tasks/search?taskName=project"

# Combined search
curl "http://localhost:8080/api/tasks/search?priority=HIGH&taskType=WORK"
```

## 🐳 Docker Setup

The PostgreSQL database can be managed with Docker Compose:

```bash
# Start PostgreSQL and pgAdmin
docker-compose up -d

# Stop services
docker-compose down

# Remove all data
docker-compose down -v
```

## 🔒 Security Considerations

For production deployment:

1. **Change default passwords** in `application-postgres.yml`
2. **Use environment variables** for sensitive data:
   ```yaml
   spring:
     datasource:
       url: ${DB_URL:jdbc:postgresql://localhost:5432/super_organizer_db}
       username: ${DB_USERNAME:postgres}
       password: ${DB_PASSWORD:password}
   ```
3. **Enable SSL/TLS** for database connections
4. **Set up proper user permissions** in PostgreSQL

## 📁 Project Structure

```
.
├── src/
│   └── main/
│       ├── kotlin/com/superorganizer/
│       │   ├── controller/          # REST controllers
│       │   ├── dto/                # Data Transfer Objects
│       │   ├── model/              # Entity models
│       │   ├── repository/         # Data repositories
│       │   ├── service/            # Business logic
│       │   └── config/             # Configuration
│       └── resources/
│           ├── application.yml      # H2 configuration
│           └── application-postgres.yml  # PostgreSQL configuration
├── database/
│   ├── schema.sql                  # PostgreSQL schema
│   ├── sample_data.sql             # Sample data
│   └── migrations/                 # Database migrations
├── docker-compose.yml              # Docker setup
├── setup_postgres.sh               # PostgreSQL setup script
└── build.gradle.kts                # Build configuration
```

## 🏃‍♂️ Running Tests

```bash
# Run all tests
./gradlew test

# Run with specific profile
./gradlew test --args='--spring.profiles.active=postgres'
```

## 📊 Performance Features

- **8 Strategic Indexes** for optimal query performance
- **Composite Indexes** for multi-column searches
- **Automatic Timestamp Updates** via triggers (PostgreSQL)
- **Connection Pooling** with HikariCP
- **Query Optimization** with JPA query hints

## 🤝 Contributing

1. Use H2 for development and testing
2. Test with PostgreSQL before deploying
3. Follow the existing code structure
4. Add tests for new features
5. Update documentation as needed

## 📚 Additional Resources

- **Swagger Documentation**: http://localhost:8080/swagger-ui.html
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **PostgreSQL Documentation**: https://www.postgresql.org/docs/
- **H2 Database Documentation**: https://www.h2database.com/html/main.html

---

**Ready to use!** Choose your preferred database setup and start building amazing task management features!