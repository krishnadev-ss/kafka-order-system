# Kafka Order System

A simple Spring Boot example demonstrating an event-driven order processing flow using Apache Kafka.

This repository contains a small Producer (REST API) that publishes `Order` events to a Kafka topic named `orders`, and a Consumer that listens for those events and processes them.

Contents
 - REST API (Producer): `POST /api/orders` — accepts order JSON and sends to Kafka
 - Kafka topic: `orders`
 - Consumer: `@KafkaListener` processes messages and logs progress

Prerequisites
 - Java 17+
 - Maven 3.6+
 - Docker & Docker Compose (for running Kafka & Zookeeper locally)

Quick start (local)

1) Start Kafka + Zookeeper with Docker Compose

```bash
cd kafka-order-system
docker-compose up -d
sleep 10
```

2) Build and run the Spring Boot application

```bash
#mvn clean install
mvn spring-boot:run
```

Application defaults
 - Base URL: http://localhost:8080
 - API base path: `/api` → full base: http://localhost:8080/api

API Endpoints

- Health

```bash
GET /api/orders/health
```

- Create order (producer)

```bash
POST /api/orders
Content-Type: application/json

{
	"orderId": "optional-ORD-id",
	"user": "string",
	"item": "string"
}
```

Response: 202 Accepted (order queued and sent to Kafka)

- Get order status (mock)

```bash
GET /api/orders/{orderId}
```

What this project demonstrates
 - Using `KafkaTemplate` to publish JSON messages to Kafka
 - Using `@KafkaListener` to consume and process messages
 - JSON serialization/deserialization with Jackson
 - Basic validation and error handling on the REST API

Project layout (key files)
 - `src/main/java/com/example/kafkaordersystem/KafkaOrderSystemApplication.java` — main class
 - `src/main/java/com/example/kafkaordersystem/config/KafkaConfig.java` — Kafka producer/consumer configuration
 - `src/main/java/com/example/kafkaordersystem/controller/OrderController.java` — REST endpoints
 - `src/main/java/com/example/kafkaordersystem/producer/OrderProducer.java` — Kafka producer service
 - `src/main/java/com/example/kafkaordersystem/consumer/OrderConsumer.java` — Kafka consumer service
 - `src/main/java/com/example/kafkaordersystem/model/Order.java` — Order model
 - `pom.xml` — project dependencies
 - `docker-compose.yml` — Kafka + Zookeeper for local testing

Useful scripts
 - `setup.sh` — automated setup (starts Docker and builds project)
 - `test-api.sh` — script that runs several API test requests
 - `build-docker.sh` — builds the application Docker image

Docker
 - `docker-compose.yml` includes Zookeeper and a Kafka broker configured for local development (bootstrap: `localhost:9092`).

Verification
 - After sending orders with `POST /api/orders`, watch the application logs. The consumer should log:

```
Received order: <orderId>
Processing order...
Order processed successfully
```

Troubleshooting
 - If Kafka connection fails: ensure `docker-compose up -d` is running and wait for services to be healthy
 - Port conflicts: change `server.port` in `src/main/resources/application.yml`

Further reading
 - See `README.md` companion docs: `TESTING.md`, `PROJECT_SETUP.md`, and `GETTING_STARTED.txt` for extended guides and test procedures.

If you want, I can now merge this content into `README.md` to include more examples, screenshots, or step-by-step screenshots for Windows/macOS specifics.