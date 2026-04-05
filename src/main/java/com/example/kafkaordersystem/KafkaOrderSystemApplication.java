package com.example.kafkaordersystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Kafka Order System - Main Application Class
 *
 * This is the entry point for the Spring Boot application.
 * The application demonstrates an event-driven architecture using Apache Kafka
 * with a Producer-Consumer pattern.
 *
 * @SpringBootApplication: Enables auto-configuration and component scanning
 * @Slf4j: Lombok annotation for creating a logger instance
 *
 * Architecture Overview:
 * ├── REST API (Producer)
 * │   └── POST /api/orders → Receives order requests
 * ├── Kafka Topic: "orders"
 * │   └── Stores order events
 * └── Consumer
 *     └── Listens and processes orders
 *
 * Flow:
 * 1. Client sends HTTP POST request with order data
 * 2. OrderController validates the request
 * 3. OrderProducer sends the order to Kafka topic "orders"
 * 4. OrderConsumer listens to the topic and processes the order
 * 5. Response returned to client
 */
@SpringBootApplication
public class KafkaOrderSystemApplication {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderSystemApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(KafkaOrderSystemApplication.class, args);
        
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║                                                                ║");
        log.info("║          🚀 KAFKA ORDER SYSTEM APPLICATION STARTED 🚀          ║");
        log.info("║                                                                ║");
        log.info("║  📍 Server: http://localhost:8080                             ║");
        log.info("║  📍 API Base: http://localhost:8080/api                        ║");
        log.info("║                                                                ║");
        log.info("║  Endpoints:                                                    ║");
        log.info("║  ├── POST /api/orders            → Create new order            ║");
        log.info("║  ├── GET  /api/orders/{id}       → Get order status            ║");
        log.info("║  └── GET  /api/orders/health     → Health check                ║");
        log.info("║                                                                ║");
        log.info("║  Kafka:                                                        ║");
        log.info("║  ├── Topic: orders                                             ║");
        log.info("║  ├── Bootstrap: localhost:9092                                 ║");
        log.info("║  └── Group: order-consumer-group                               ║");
        log.info("║                                                                ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");
    }
}
