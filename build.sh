#!/bin/bash

# Build and run script for Super Organizer API

set -e

echo "🚀 Building and running Super Organizer API with Docker..."

# Function to display usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo "Options:"
    echo "  -h, --help          Show this help message"
    echo "  -b, --build         Build the Docker image"
    echo "  -r, --run           Run the application with docker-compose"
    echo "  -p, --production    Run in production mode"
    echo "  -s, --stop          Stop all containers"
    echo "  -c, --clean         Clean up containers and images"
    echo "  -f, --full          Build and run (default)"
}

# Function to build the application
build() {
    echo "📦 Building Docker image..."
    docker build -t super-organizer-api .
    echo "✅ Build completed successfully!"
}

# Function to run the application
run() {
    local compose_file="docker-compose.yml"
    if [[ "$1" == "production" ]]; then
        compose_file="docker-compose.prod.yml"
        echo "🏭 Running in production mode..."
    else
        echo "🛠️  Running in development mode..."
    fi
    
    docker-compose -f $compose_file up -d
    echo "✅ Application started!"
    echo "📊 Services:"
    echo "   - API: http://localhost:8080"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - PgAdmin: http://localhost:8081"
    echo "   - Database: localhost:5432"
}

# Function to stop containers
stop() {
    echo "🛑 Stopping containers..."
    docker-compose down
    docker-compose -f docker-compose.prod.yml down 2>/dev/null || true
    echo "✅ Containers stopped!"
}

# Function to clean up
clean() {
    echo "🧹 Cleaning up..."
    docker-compose down -v
    docker-compose -f docker-compose.prod.yml down -v 2>/dev/null || true
    docker rmi super-organizer-api 2>/dev/null || true
    docker system prune -f
    echo "✅ Cleanup completed!"
}

# Parse command line arguments
BUILD=false
RUN=false
PRODUCTION=false
STOP=false
CLEAN=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            usage
            exit 0
            ;;
        -b|--build)
            BUILD=true
            shift
            ;;
        -r|--run)
            RUN=true
            shift
            ;;
        -p|--production)
            PRODUCTION=true
            RUN=true
            shift
            ;;
        -s|--stop)
            STOP=true
            shift
            ;;
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -f|--full)
            BUILD=true
            RUN=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
done

# Default behavior if no options provided
if [[ "$BUILD" == false && "$RUN" == false && "$STOP" == false && "$CLEAN" == false ]]; then
    BUILD=true
    RUN=true
fi

# Execute actions
if [[ "$CLEAN" == true ]]; then
    clean
fi

if [[ "$STOP" == true ]]; then
    stop
fi

if [[ "$BUILD" == true ]]; then
    build
fi

if [[ "$RUN" == true ]]; then
    if [[ "$PRODUCTION" == true ]]; then
        run "production"
    else
        run
    fi
fi

echo "🎉 Done!"