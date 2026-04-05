#!/bin/bash

# Build Docker image for the Spring Boot application
# Usage: ./build-docker.sh [--push] [--tag VERSION]

set -e

REGISTRY="docker.io"
USERNAME="your-docker-username"
IMAGE_NAME="kafka-order-system"
VERSION="1.0.0"
PUSH_IMAGE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --push)
            PUSH_IMAGE=true
            shift
            ;;
        --tag)
            VERSION="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                                                                ║"
echo "║          🐳 BUILDING DOCKER IMAGE FOR KAFKA-ORDER-SYSTEM 🐳   ║"
echo "║                                                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Build with Maven first
echo "🔨 Building application with Maven..."
mvn clean package -DskipTests

# Create Dockerfile in workspace root (if it doesn't exist)
if [ ! -f "Dockerfile" ]; then
    echo "📝 Creating Dockerfile..."
    cat > Dockerfile << 'EOF'
# Multi-stage build
FROM maven:3.8.6-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Final image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/kafka-order-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
    echo "✅ Dockerfile created"
fi

# Build Docker image
echo ""
echo "🐳 Building Docker image..."
docker build -t $USERNAME/$IMAGE_NAME:$VERSION \
             -t $USERNAME/$IMAGE_NAME:latest \
             .

echo "✅ Docker image built successfully!"
echo ""
echo "📊 Image details:"
docker image inspect $USERNAME/$IMAGE_NAME:$VERSION | jq '.[] | {Architecture, Os, Size}'
echo ""

# Push image if --push flag is provided
if [ "$PUSH_IMAGE" = true ]; then
    echo "📤 Pushing image to registry..."
    docker push $USERNAME/$IMAGE_NAME:$VERSION
    docker push $USERNAME/$IMAGE_NAME:latest
    echo "✅ Image pushed successfully!"
fi

echo ""
echo "🎉 Build complete!"
echo ""
echo "Next steps:"
echo "  1. Run the image: docker run -p 8080:8080 --network kafka-network $USERNAME/$IMAGE_NAME:$VERSION"
echo "  2. Or push: ./build-docker.sh --push"
echo ""
