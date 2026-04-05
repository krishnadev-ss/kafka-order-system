package com.example.kafkaordersystem.consumer;

import com.example.kafkaordersystem.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * OrderConsumer Service
 *
 * This service listens to messages from the Kafka "orders" topic.
 * It automatically processes each order message as it arrives.
 *
 * @KafkaListener: Annotation that registers this method as a message listener
 * @Service: Marks this class as a Spring service component
 */
@Service
public class OrderConsumer {

     private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    /**
     * Kafka listener method for consuming orders
     *
     * This method is automatically called whenever a message arrives on the "orders" topic.
     * The message is automatically deserialized from JSON to an Order object.
     *
     * @param order The deserialized Order object from the Kafka message
     *
     * @KafkaListener Parameters:
     *   - topics: The Kafka topic(s) to listen to
     *   - groupId: Consumer group identifier (used for tracking consumer progress)
     *   - containerFactory: The factory bean used to create the listener container
     *   - concurrency: Number of concurrent consumers (default: 3 from config)
     */
    @KafkaListener(
        topics = "${spring.kafka.topic.name:orders}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrder(Order order) {
        log.info("");
        log.info("╔════════════════════════════════════════════╗");
        log.info("║        📨 ORDER CONSUMER TRIGGERED          ║");
        log.info("╚════════════════════════════════════════════╝");

        try {
            // Step 1: Receive the order
            log.info("📥 Received order: {}", order.getOrderId());
            log.info("   User: {}", order.getUser());
            log.info("   Item: {}", order.getItem());
            log.info("   Timestamp: {}", order.getTimestamp());

            // Step 2: Start processing the order
            log.info("⏳ Processing order...");
            
            // Simulate some processing time
            Thread.sleep(1000);

            // Step 3: Mark as processed
            log.info("✅ Order processed successfully!");
            log.info("   Order ID: {}", order.getOrderId());
            log.info("   Status: COMPLETED");
            log.info("");

            // In a real application, you would:
            // 1. Store the order in a database
            // 2. Update order status
            // 3. Trigger downstream services
            // 4. Send notifications

        } catch (InterruptedException e) {
            log.error("⚠️  Order processing interrupted: {}", order.getOrderId(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Error handling: Log the error and optionally send to a dead letter queue
            log.error("❌ Error processing order: {}", order.getOrderId(), e);
            
            // You could implement a dead letter queue here:
            // 1. Send to a separate "dead-letters" or "orders-dlq" topic
            // 2. Log to persistent storage for manual review
            // 3. Alert the operations team
            
            // Rethrow or handle based on your error handling strategy
            throw new RuntimeException("Order processing failed", e);
        }
    }

    /**
     * Alternative listener for batch processing (optional)
     *
     * You can create multiple listeners for different purposes:
     * - One for real-time processing
     * - One for batch aggregation
     * - One for archival/logging
     *
     * Note: This method is commented out as an example.
     * Uncomment and configure as needed.
     */
    /*
    @KafkaListener(
        topics = "${spring.kafka.topic.name:orders}",
        groupId = "batch-consumer-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderBatch(Order order) {
        // Batch processing logic would go here
        log.debug("Batch processing order: {}", order.getOrderId());
    }
    */
}
