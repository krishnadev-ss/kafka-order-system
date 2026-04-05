package com.example.kafkaordersystem.producer;

import com.example.kafkaordersystem.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * OrderProducer Service
 *
 * This service is responsible for sending order messages to the Kafka "orders" topic.
 * It uses KafkaTemplate to send messages asynchronously.
 *
 * @Slf4j: Lombok annotation that creates a logger instance for this class
 * @Service: Marks this class as a Spring service component (will be auto-wired)
 */
@Slf4j
@Service
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    @Value("${spring.kafka.topic.name:orders}")
    private String orderTopic;

    /**
     * Sends an order message to the Kafka topic
     *
     * @param order The Order object to be sent
     * @return CompletableFuture<Void> representing the async operation
     */
    public CompletableFuture<Void> sendOrder(Order order) {
        log.info("📤 Preparing to send order: {}", order.getOrderId());

        // Create a Kafka message with the order data
        // The key is the orderId (used for partitioning)
        // The value is the Order object (will be serialized to JSON)
        Message<Order> message = MessageBuilder
            .withPayload(order)
            .setHeader(KafkaHeaders.TOPIC, orderTopic)
            .setHeader(KafkaHeaders.MESSAGE_KEY, order.getOrderId())
            .build();

        // Send the message asynchronously and handle the result
        return kafkaTemplate.send(message)
            .thenAccept(result -> {
                // On success, log the details
                log.info("✅ Order sent successfully!");
                log.info("   Order ID: {}", order.getOrderId());
                log.info("   Topic: {}", result.getRecordMetadata().topic());
                log.info("   Partition: {}", result.getRecordMetadata().partition());
                log.info("   Offset: {}", result.getRecordMetadata().offset());
            })
            .exceptionally(e -> {
                // On error, log the exception details
                log.error("❌ Failed to send order: {}", order.getOrderId(), e);
                throw new RuntimeException("Failed to send order to Kafka", e);
            });
    }

    /**
     * Alternative method: Sends order using sendDefault (simpler approach)
     * Useful for quick testing
     *
     * @param order The Order object to be sent
     */
    public void sendOrderSimple(Order order) {
        log.info("📤 Sending order (simple): {}", order.getOrderId());
        try {
            kafkaTemplate.send(orderTopic, order.getOrderId(), order)
                .get(); // Block until the message is sent (not recommended for production)
            log.info("✅ Order sent successfully: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("❌ Error sending order: {}", order.getOrderId(), e);
            throw new RuntimeException("Failed to send order", e);
        }
    }
}
