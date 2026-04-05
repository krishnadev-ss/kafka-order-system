# Makefile for Kafka Order System

# Variables
APP_NAME = kafka-order-system
JAVA_VERSION = 17
MAVEN_CMD = mvn
DOCKER_COMPOSE = docker-compose

# Colors for output
BLUE = \033[0;34m
GREEN = \033[0;32m
YELLOW = \033[0;33m
NC = \033[0m # No Color

.PHONY: help build run clean docker-up docker-down stop reset test logs tail

help:
	@echo "$(BLUE)═══════════════════════════════════════════════════════$(NC)"
	@echo "$(BLUE)  Kafka Order System - Available Commands$(NC)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════$(NC)"
	@echo "$(GREEN)make build$(NC)           - Build the project with Maven"
	@echo "$(GREEN)make run$(NC)             - Run the Spring Boot application"
	@echo "$(GREEN)make docker-up$(NC)       - Start Kafka and Zookeeper containers"
	@echo "$(GREEN)make docker-down$(NC)     - Stop Kafka and Zookeeper containers"
	@echo "$(GREEN)make clean$(NC)           - Clean Maven build artifacts"
	@echo "$(GREEN)make test$(NC)            - Run tests"
	@echo "$(GREEN)make logs$(NC)            - View Docker container logs"
	@echo "$(GREEN)make tail$(NC)            - Follow Docker container logs (live)"
	@echo "$(GREEN)make reset$(NC)           - Reset everything (clean, docker-down, etc.)"
	@echo "$(GREEN)make setup$(NC)           - Setup everything (docker-up + build)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════$(NC)"

# Build the project
build:
	@echo "$(YELLOW)Building project...$(NC)"
	$(MAVEN_CMD) clean install
	@echo "$(GREEN)✅ Build complete!$(NC)"

# Run the application
run:
	@echo "$(YELLOW)Running application...$(NC)"
	$(MAVEN_CMD) spring-boot:run

# Start Kafka and Zookeeper
docker-up:
	@echo "$(YELLOW)Starting Kafka and Zookeeper...$(NC)"
	$(DOCKER_COMPOSE) up -d
	@echo "$(GREEN)✅ Containers started!$(NC)"
	@echo "$(BLUE)Waiting for Kafka to be ready... (20 seconds)$(NC)"
	@sleep 20
	@echo "$(GREEN)✅ Kafka is ready!$(NC)"

# Stop Kafka and Zookeeper
docker-down:
	@echo "$(YELLOW)Stopping containers...$(NC)"
	$(DOCKER_COMPOSE) down
	@echo "$(GREEN)✅ Containers stopped!$(NC)"

# Stop Kafka and Zookeeper (alias)
stop: docker-down

# Clean build artifacts
clean:
	@echo "$(YELLOW)Cleaning Maven artifacts...$(NC)"
	$(MAVEN_CMD) clean
	@echo "$(GREEN)✅ Cleaned!$(NC)"

# Run tests
test:
	@echo "$(YELLOW)Running tests...$(NC)"
	$(MAVEN_CMD) test

# View container logs
logs:
	@echo "$(BLUE)View logs:$(NC)"
	@echo "  Kafka:     $(DOCKER_COMPOSE) logs kafka"
	@echo "  Zookeeper: $(DOCKER_COMPOSE) logs zookeeper"
	@echo "  All:       $(DOCKER_COMPOSE) logs"
	$(DOCKER_COMPOSE) logs

# Follow container logs (live)
tail:
	@echo "$(YELLOW)Following logs (Ctrl+C to stop)...$(NC)"
	$(DOCKER_COMPOSE) logs -f

# Reset everything
reset: clean docker-down
	@echo "$(YELLOW)Resetting everything...$(NC)"
	@rm -rf target/
	@echo "$(GREEN)✅ Reset complete!$(NC)"

# Setup everything
setup: docker-up build
	@echo "$(GREEN)✅ Setup complete!$(NC)"
	@echo "$(BLUE)Next steps:$(NC)"
	@echo "  1. Run: make run"
	@echo "  2. Open: http://localhost:8080/api/orders/health"

# Package the application
package:
	@echo "$(YELLOW)Packaging application...$(NC)"
	$(MAVEN_CMD) package -DskipTests
	@echo "$(GREEN)✅ Package complete!$(NC)"

# Run the packaged JAR
run-jar: package
	@echo "$(YELLOW)Running JAR...$(NC)"
	java -jar target/$(APP_NAME)-1.0.0.jar

# Install dependencies
install-deps:
	@echo "$(YELLOW)Installing dependencies...$(NC)"
	$(MAVEN_CMD) dependency:resolve
	@echo "$(GREEN)✅ Dependencies installed!$(NC)"

# Format code
format:
	@echo "$(YELLOW)Formatting code...$(NC)"
	$(MAVEN_CMD) fmt:format

# Check for dependency vulnerabilities
check-security:
	@echo "$(YELLOW)Checking for vulnerabilities...$(NC)"
	$(MAVEN_CMD) org.owasp:dependency-check-maven:check

# Quick start (docker + build + run)
quickstart: docker-up build run

# CI/CD pipeline (clean, test, package)
ci: clean test package
	@echo "$(GREEN)✅ CI pipeline complete!$(NC)"

# Status check
status:
	@echo "$(BLUE)═══════════════════════════════════════════════════════$(NC)"
	@echo "$(CYAN)  System Status$(NC)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════$(NC)"
	@echo "$(YELLOW)Docker Containers:$(NC)"
	@$(DOCKER_COMPOSE) ps
	@echo ""
	@echo "$(YELLOW)Running Processes:$(NC)"
	@pgrep -f "spring-boot" > /dev/null && echo "✅ Spring Boot is running" || echo "❌ Spring Boot is NOT running"
	@echo ""

.DEFAULT_GOAL := help
