# Makefile for Super Organizer API Docker operations

.PHONY: help build run stop clean logs test validate

# Default target
help:
	@echo "Super Organizer API - Docker Operations"
	@echo ""
	@echo "Available commands:"
	@echo "  make build      - Build the Docker image"
	@echo "  make run        - Run the application (development)"
	@echo "  make run-prod   - Run the application (production)"
	@echo "  make stop       - Stop all containers"
	@echo "  make clean      - Clean up containers and images"
	@echo "  make logs       - View application logs"
	@echo "  make test       - Run tests"
	@echo "  make validate   - Validate Docker setup"
	@echo "  make help       - Show this help message"

# Build the Docker image
build:
	@echo "📦 Building Docker image..."
	docker build -t super-organizer-api .
	@echo "✅ Build completed!"

# Run in development mode
run:
	@echo "🛠️  Starting application in development mode..."
	docker-compose up -d
	@echo "✅ Application started!"
	@echo "📊 Services available at:"
	@echo "   - API: http://localhost:8080"
	@echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
	@echo "   - PgAdmin: http://localhost:8081"

# Run in production mode
run-prod:
	@echo "🏭 Starting application in production mode..."
	docker-compose -f docker-compose.prod.yml up -d
	@echo "✅ Application started in production mode!"
	@echo "📊 Services available at:"
	@echo "   - API: http://localhost:8080"

# Stop all containers
stop:
	@echo "🛑 Stopping containers..."
	docker-compose down
	docker-compose -f docker-compose.prod.yml down 2>/dev/null || true
	@echo "✅ Containers stopped!"

# Clean up everything
clean:
	@echo "🧹 Cleaning up..."
	docker-compose down -v
	docker-compose -f docker-compose.prod.yml down -v 2>/dev/null || true
	docker rmi super-organizer-api 2>/dev/null || true
	docker system prune -f
	@echo "✅ Cleanup completed!"

# View logs
logs:
	@echo "📋 Application logs:"
	docker-compose logs -f app

# Run tests
test:
	@echo "🧪 Running tests..."
	./gradlew test

# Validate Docker setup
validate:
	@echo "🔍 Validating Docker setup..."
	./validate-docker.sh

# Quick start (build and run)
start: build run

# Quick start production (build and run production)
start-prod: build run-prod