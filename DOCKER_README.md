# Docker Setup for Super Organizer API

This document provides instructions for building and running the Super Organizer API using Docker.

## Prerequisites

- Docker (version 20.10 or higher)
- Docker Compose (version 2.0 or higher)

## Quick Start

### Option 1: Using the Build Script (Recommended)

The easiest way to build and run the application:

```bash
# Build and run in development mode
./build.sh

# Build and run in production mode
./build.sh --production

# Build only
./build.sh --build

# Run only (assumes image is already built)
./build.sh --run

# Stop all containers
./build.sh --stop

# Clean up everything
./build.sh --clean
```

### Option 2: Manual Docker Commands

#### Development Mode

```bash
# Build the Docker image
docker build -t super-organizer-api .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

#### Production Mode

```bash
# Build the Docker image
docker build -t super-organizer-api .

# Run in production mode
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f app

# Stop services
docker-compose -f docker-compose.prod.yml down
```

## Services

Once running, the following services will be available:

| Service | URL | Description |
|---------|-----|-------------|
| API | http://localhost:8080 | Main application API |
| Swagger UI | http://localhost:8080/swagger-ui.html | API documentation |
| PgAdmin | http://localhost:8081 | PostgreSQL admin interface |
| Database | localhost:5432 | PostgreSQL database |

### Default Credentials

- **PgAdmin**: admin@superorganizer.com / admin
- **Database**: postgres / password

## Environment Variables

### Development
- `SPRING_PROFILES_ACTIVE=postgres`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/super_organizer_db`
- `SPRING_DATASOURCE_USERNAME=postgres`
- `SPRING_DATASOURCE_PASSWORD=password`

### Production
- `POSTGRES_PASSWORD`: Set this environment variable for production database password
- `JAVA_OPTS`: JVM options (default: `-Xmx1g -Xms512m`)

## Docker Images

### Application Image
- **Base**: `openjdk:17-jre-slim`
- **Size**: ~200MB (optimized)
- **Security**: Runs as non-root user
- **Health Check**: Built-in health check endpoint

### Database Image
- **Base**: `postgres:15-alpine`
- **Persistence**: Data stored in Docker volume
- **Initialization**: Schema and sample data automatically loaded

## File Structure

```
.
├── Dockerfile                    # Multi-stage build for the application
├── .dockerignore                 # Files to exclude from build context
├── docker-compose.yml           # Development environment
├── docker-compose.prod.yml      # Production environment
├── build.sh                     # Build and run script
└── DOCKER_README.md            # This file
```

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using the port
   sudo lsof -i :8080
   
   # Stop conflicting services
   sudo systemctl stop conflicting-service
   ```

2. **Database Connection Issues**
   ```bash
   # Check if PostgreSQL is running
   docker-compose logs postgres
   
   # Restart the database
   docker-compose restart postgres
   ```

3. **Application Won't Start**
   ```bash
   # Check application logs
   docker-compose logs app
   
   # Check if database is ready
   docker-compose exec postgres pg_isready -U postgres
   ```

4. **Build Failures**
   ```bash
   # Clean build cache
   docker system prune -a
   
   # Rebuild without cache
   docker build --no-cache -t super-organizer-api .
   ```

### Health Checks

The application includes health checks for both the app and database:

- **App Health Check**: `http://localhost:8080/actuator/health`
- **Database Health Check**: PostgreSQL readiness check

### Logs

View logs for specific services:

```bash
# Application logs
docker-compose logs -f app

# Database logs
docker-compose logs -f postgres

# All logs
docker-compose logs -f
```

## Performance Optimization

### Development
- Uses H2 in-memory database by default
- Hot reload enabled
- Debug mode enabled

### Production
- PostgreSQL with persistent storage
- Optimized JVM settings
- Health checks enabled
- Non-root user for security

## Security Considerations

1. **Non-root User**: Application runs as `appuser`
2. **Minimal Base Image**: Uses slim JRE image
3. **Environment Variables**: Sensitive data passed via environment variables
4. **Network Isolation**: Services communicate via Docker network

## Scaling

For production deployments, consider:

1. **Database**: Use managed PostgreSQL service
2. **Load Balancer**: Add reverse proxy (nginx/traefik)
3. **Monitoring**: Add Prometheus/Grafana
4. **Logging**: Centralized logging (ELK stack)

## Contributing

When making changes to the Docker setup:

1. Test both development and production configurations
2. Update this README if needed
3. Ensure security best practices are followed
4. Test with different environments (Linux, macOS, Windows)