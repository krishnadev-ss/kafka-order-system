package com.example.kafkaordersystem.producer;

import com.example.kafkaordersystem.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * OrderProducer Service
 *
 * This service is responsible for sending order messages to the Kafka "orders" topic.
 * It uses KafkaTemplate to send messages asynchronously and converts the result
 * to a CompletableFuture for easier async handling.
 */
@Service
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

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

        CompletableFuture<SendResult<String, Order>> future =
            kafkaTemplate.send(orderTopic, order.getOrderId(), order);

        CompletableFuture<Void> completable = new CompletableFuture<>();
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("❌ Failed to send order: {}", order.getOrderId(), ex);
                completable.completeExceptionally(new RuntimeException("Failed to send order to Kafka", ex));
            } else {
                log.info("✅ Order sent successfully!");
                log.info("   Order ID: {}", order.getOrderId());
                log.info("   Topic: {}", result.getRecordMetadata().topic());
                log.info("   Partition: {}", result.getRecordMetadata().partition());
                log.info("   Offset: {}", result.getRecordMetadata().offset());
                completable.complete(null);
            }
        });

        return completable;
    }

    /**
     * Alternative synchronous method for quick testing
     */
    public void sendOrderSimple(Order order) {
        log.info("📤 Sending order (simple): {}", order.getOrderId());
        try {
            kafkaTemplate.send(orderTopic, order.getOrderId(), order).get();
            log.info("✅ Order sent successfully: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("❌ Error sending order: {}", order.getOrderId(), e);
            throw new RuntimeException("Failed to send order", e);
        }
    }
}
