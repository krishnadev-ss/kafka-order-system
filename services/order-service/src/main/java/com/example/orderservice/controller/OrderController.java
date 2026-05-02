package com.example.orderservice.controller;

import com.example.orderservice.api.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.producer.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderProducer producer;

    public OrderController(OrderProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        if (request.getUser() == null || request.getUser().trim().isEmpty()
                || request.getItem() == null || request.getItem().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("user and item are required");
        }

        String orderId = (request.getOrderId() != null && !request.getOrderId().isBlank())
                ? request.getOrderId()
                : UUID.randomUUID().toString();

        Order order = new Order(orderId, request.getUser().trim(), request.getItem().trim(), "PENDING", Instant.now());

        log.info("Order received: {}", order);
        producer.sendOrder(order);
        log.info("Order sent to Kafka: {}", order.getOrderId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(order);
    }
}

