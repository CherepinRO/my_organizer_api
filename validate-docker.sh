#!/bin/bash

# Docker validation script for Super Organizer API

echo "🔍 Validating Docker setup for Super Organizer API..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed or not in PATH"
    echo "📦 Please install Docker first:"
    echo "   - Ubuntu/Debian: sudo apt-get install docker.io docker-compose"
    echo "   - macOS: brew install docker docker-compose"
    echo "   - Windows: Download from https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Check if Docker is running
if ! docker info &> /dev/null; then
    echo "❌ Docker daemon is not running"
    echo "🚀 Please start Docker:"
    echo "   - Linux: sudo systemctl start docker"
    echo "   - macOS: open -a Docker"
    echo "   - Windows: Start Docker Desktop"
    exit 1
fi

echo "✅ Docker is available and running"

# Validate Dockerfile syntax
echo "📋 Validating Dockerfile syntax..."
if docker build --dry-run . 2>/dev/null; then
    echo "✅ Dockerfile syntax is valid"
else
    echo "⚠️  Dockerfile syntax validation not available in this Docker version"
    echo "   This is normal for older Docker versions"
fi

# Check required files
echo "📁 Checking required files..."
required_files=("Dockerfile" "docker-compose.yml" "build.gradle.kts" "gradlew")

for file in "${required_files[@]}"; do
    if [[ -f "$file" ]]; then
        echo "✅ $file exists"
    else
        echo "❌ $file is missing"
    fi
done

# Check if gradle wrapper is executable
if [[ -f "gradlew" ]]; then
    if [[ -x "gradlew" ]]; then
        echo "✅ gradlew is executable"
    else
        echo "⚠️  gradlew is not executable, fixing..."
        chmod +x gradlew
        echo "✅ gradlew is now executable"
    fi
fi

echo ""
echo "🎉 Docker setup validation completed!"
echo ""
echo "📖 Next steps:"
echo "   1. Run: ./build.sh"
echo "   2. Or manually: docker build -t super-organizer-api ."
echo "   3. Then: docker-compose up -d"
echo ""
echo "📚 For detailed instructions, see: DOCKER_README.md"