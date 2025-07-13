#!/bin/bash

# Super Organizer API - PostgreSQL Setup Script

echo "🚀 Setting up PostgreSQL database for Super Organizer API..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Create database directory if it doesn't exist
mkdir -p database/migrations

# Start PostgreSQL with Docker Compose
echo "📦 Starting PostgreSQL container..."
docker-compose up -d postgres

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 10

# Check if PostgreSQL is running
if docker-compose ps postgres | grep -q "Up"; then
    echo "✅ PostgreSQL is running!"
else
    echo "❌ PostgreSQL failed to start. Check logs:"
    docker-compose logs postgres
    exit 1
fi

# Test database connection
echo "🔍 Testing database connection..."
if docker-compose exec postgres psql -U postgres -d super_organizer_db -c "SELECT 1;" &> /dev/null; then
    echo "✅ Database connection successful!"
else
    echo "❌ Database connection failed."
    exit 1
fi

# Check if tables exist
echo "🔍 Checking database tables..."
TABLE_COUNT=$(docker-compose exec postgres psql -U postgres -d super_organizer_db -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'tasks';" | tr -d ' ')

if [ "$TABLE_COUNT" -eq 1 ]; then
    echo "✅ Database tables exist!"
    
    # Count records in tasks table
    RECORD_COUNT=$(docker-compose exec postgres psql -U postgres -d super_organizer_db -t -c "SELECT COUNT(*) FROM tasks;" | tr -d ' ')
    echo "📊 Tasks table contains $RECORD_COUNT records"
else
    echo "❌ Database tables don't exist."
    exit 1
fi

# Display connection information
echo ""
echo "🎉 PostgreSQL setup complete!"
echo ""
echo "📋 Connection Information:"
echo "  Host: localhost"
echo "  Port: 5432"
echo "  Database: super_organizer_db"
echo "  Username: postgres"
echo "  Password: password"
echo ""
echo "🌐 Access URLs:"
echo "  pgAdmin: http://localhost:8080"
echo "  pgAdmin Login: admin@superorganizer.com / admin"
echo ""
echo "🔧 To run the Spring Boot application with PostgreSQL:"
echo "  ./gradlew bootRun --args='--spring.profiles.active=postgres'"
echo ""
echo "🛑 To stop the containers:"
echo "  docker-compose down"
echo ""
echo "🗑️  To remove all data:"
echo "  docker-compose down -v"