# Super Organizer API - PostgreSQL Database Setup

This directory contains the PostgreSQL database implementation for the Super Organizer API.

## 🗄️ Database Structure

### Main Table: `tasks`
- **id**: Primary key (BIGSERIAL)
- **date**: Task date (DATE, NOT NULL)
- **task_name**: Task name (VARCHAR(255), NOT NULL)
- **comment**: Optional comment (TEXT)
- **deadline**: Optional deadline (TIMESTAMP)
- **priority**: Task priority (ENUM: HIGH, MEDIUM, LOW)
- **task_type**: Task type (ENUM: WORK, HOME)
- **created_at**: Creation timestamp (TIMESTAMP, NOT NULL)
- **updated_at**: Last update timestamp (TIMESTAMP, NOT NULL)

### Enums
- **priority_enum**: `HIGH`, `MEDIUM`, `LOW`
- **task_type_enum**: `WORK`, `HOME`

## 🚀 Quick Start with Docker

### Prerequisites
- Docker
- Docker Compose

### Run PostgreSQL with Docker Compose
```bash
# Start PostgreSQL and pgAdmin
docker-compose up -d

# Check if containers are running
docker-compose ps

# View logs
docker-compose logs postgres
```

### Access Information
- **PostgreSQL**: localhost:5432
- **pgAdmin**: http://localhost:8080
  - Email: admin@superorganizer.com
  - Password: admin

### Database Connection Details
- **Host**: localhost
- **Port**: 5432
- **Database**: super_organizer_db
- **Username**: postgres
- **Password**: password

## 🔧 Manual Setup

### 1. Install PostgreSQL
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# macOS with Homebrew
brew install postgresql

# Start PostgreSQL service
sudo systemctl start postgresql  # Linux
brew services start postgresql   # macOS
```

### 2. Create Database and User
```bash
# Connect to PostgreSQL as superuser
sudo -u postgres psql

# Create database
CREATE DATABASE super_organizer_db;

# Create user (optional)
CREATE USER organizer_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE super_organizer_db TO organizer_user;

# Exit PostgreSQL
\q
```

### 3. Run Database Schema
```bash
# Run the schema script
psql -U postgres -d super_organizer_db -f database/schema.sql

# Load sample data
psql -U postgres -d super_organizer_db -f database/sample_data.sql
```

## 📱 Spring Boot Configuration

### Update Application Properties
Create or update `application-postgres.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/super_organizer_db
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
    show-sql: true
```

### Run Application with PostgreSQL Profile
```bash
# Run with PostgreSQL profile
./gradlew bootRun --args='--spring.profiles.active=postgres'

# Or set environment variable
export SPRING_PROFILES_ACTIVE=postgres
./gradlew bootRun
```

## 🔍 Database Queries

### Common Queries

#### Get all tasks
```sql
SELECT * FROM tasks ORDER BY priority DESC, date ASC;
```

#### Get high priority tasks
```sql
SELECT * FROM tasks WHERE priority = 'HIGH';
```

#### Get tasks with deadlines
```sql
SELECT * FROM tasks WHERE deadline IS NOT NULL ORDER BY deadline ASC;
```

#### Get work tasks from today
```sql
SELECT * FROM tasks WHERE task_type = 'WORK' AND date = CURRENT_DATE;
```

#### Search tasks by name
```sql
SELECT * FROM tasks WHERE task_name ILIKE '%project%';
```

## 📊 Performance Optimizations

### Indexes Created
- `idx_tasks_date`: For date filtering
- `idx_tasks_priority`: For priority filtering
- `idx_tasks_task_type`: For task type filtering
- `idx_tasks_deadline`: For deadline filtering
- `idx_tasks_task_name`: For task name searching
- `idx_tasks_created_at`: For creation time queries
- `idx_tasks_priority_type`: Composite index for priority + type queries
- `idx_tasks_date_priority`: Composite index for date + priority queries

### Automatic Timestamp Updates
The database includes a trigger that automatically updates the `updated_at` column whenever a task is modified.

## 🛡️ Security Considerations

### For Production
1. **Change default passwords**
2. **Use environment variables** for database credentials
3. **Enable SSL/TLS** for database connections
4. **Set up proper user permissions**
5. **Regular backups**

### Environment Variables
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=super_organizer_db
export DB_USERNAME=postgres
export DB_PASSWORD=your_secure_password
```

## 🔄 Migrations

The `migrations/` directory contains versioned migration scripts:
- `V1__Initial_schema.sql`: Initial database schema

For production, consider using Flyway or Liquibase for database migrations.

## 📈 Monitoring

### Database Health Check
```sql
SELECT 
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation
FROM pg_stats
WHERE tablename = 'tasks';
```

### Performance Monitoring
```sql
-- Check table size
SELECT pg_size_pretty(pg_relation_size('tasks'));

-- Check index usage
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
WHERE tablename = 'tasks';
```

## 🚨 Troubleshooting

### Common Issues

#### Connection refused
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql

# Check if port is available
netstat -an | grep 5432
```

#### Permission denied
```bash
# Check PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-*.log
```

#### Database doesn't exist
```bash
# List databases
psql -U postgres -l

# Create database if missing
createdb -U postgres super_organizer_db
```

## 📋 Maintenance

### Backup
```bash
# Full backup
pg_dump -U postgres -h localhost super_organizer_db > backup.sql

# Compressed backup
pg_dump -U postgres -h localhost super_organizer_db | gzip > backup.sql.gz
```

### Restore
```bash
# Restore from backup
psql -U postgres -h localhost super_organizer_db < backup.sql
```

### Vacuum and Analyze
```sql
-- Optimize table
VACUUM ANALYZE tasks;

-- Check table statistics
SELECT * FROM pg_stat_user_tables WHERE relname = 'tasks';
```

## 📚 Additional Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Boot Data JPA](https://spring.io/projects/spring-data-jpa)
- [pgAdmin Documentation](https://www.pgadmin.org/docs/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)