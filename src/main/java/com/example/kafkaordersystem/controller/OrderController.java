package com.example.kafkaordersystem.controller;

import com.example.kafkaordersystem.model.Order;
import com.example.kafkaordersystem.producer.OrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OrderController
 *
 * REST API controller for handling order-related HTTP requests.
 * Provides endpoints for creating and managing orders.
 *
 * @RestController: Marks this class as a controller with @ResponseBody on all methods
 * @RequestMapping: Maps all endpoints to /orders path
 * @Slf4j: Lombok annotation for logging
 */
@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderProducer orderProducer;

    /**
     * Health check endpoint
     * GET /api/orders/health
     *
     * @return Simple health status response
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "✅ Order Service is running");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    /**
     * Create and send a new order
     * POST /api/orders
     *
     * This endpoint receives an order request, optionally generates an order ID,
     * and sends it to the Kafka "orders" topic for processing.
     *
     * @param order The Order object from the request body (JSON)
     * @return ResponseEntity with success/error message
     *
     * Example Request:
     * {
     *   "orderId": "ORD-12345",
     *   "user": "john@example.com",
     *   "item": "Laptop"
     * }
     *
     * Example Response:
     * {
     *   "status": "success",
     *   "message": "Order received and sent to processing queue",
     *   "orderId": "ORD-12345",
     *   "timestamp": 1704067200000
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order order) {
        log.info("📨 Received order creation request for user: {}", order.getUser());

        try {
            // Validate input
            if (order.getUser() == null || order.getUser().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("User field is required"));
            }

            if (order.getItem() == null || order.getItem().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Item field is required"));
            }

            // Generate order ID if not provided
            if (order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
                order.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }

            // Set timestamp if not already set
            if (order.getTimestamp() == null) {
                order.setTimestamp(System.currentTimeMillis());
            }

            // Set initial status
            if (order.getStatus() == null) {
                order.setStatus("PENDING");
            }

            log.info("✅ Order validated. Sending to Kafka topic...");

            // Send order to Kafka (async operation)
            orderProducer.sendOrder(order)
                .thenAccept(v -> log.info("✅ Order processing initiated: {}", order.getOrderId()))
                .exceptionally(e -> {
                    log.error("❌ Failed to process order: {}", order.getOrderId(), e);
                    return null;
                });

            // Return success response immediately
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(createSuccessResponse(order));

        } catch (Exception e) {
            log.error("❌ Error processing order: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Get order status
     * GET /api/orders/{orderId}
     *
     * In a real application, this would query a database for order status.
     *
     * @param orderId The order identifier
     * @return Order status information
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable String orderId) {
        log.info("📋 Fetching status for order: {}", orderId);

        // In a real application, you would query the database here
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", "PROCESSING");
        response.put("message", "Order is being processed by the system");

        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to create a success response
     */
    private Map<String, Object> createSuccessResponse(Order order) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Order received and sent to processing queue");
        response.put("orderId", order.getOrderId());
        response.put("user", order.getUser());
        response.put("item", order.getItem());
        response.put("timestamp", order.getTimestamp());
        response.put("orderStatus", order.getStatus());
        return response;
    }

    /**
     * Helper method to create an error response
     */
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", errorMessage);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
