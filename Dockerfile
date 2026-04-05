# Multi-stage Dockerfile for Kafka Order System

# Stage 1: Build stage
FROM maven:3.8.6-openjdk-17 as builder

WORKDIR /app

# Copy pom.xml and download dependencies (leverages Docker cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/kafka-order-system-1.0.0.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.JarLauncher \
    && curl -f http://localhost:8080/api/orders/health || exit 1

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--server.port=8080"]
