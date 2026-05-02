package com.example.notificationservice.consumer;

import com.example.notificationservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "orders", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(Order order) {
        log.info("NotificationService received order {} for user {}: item={}", order.getOrderId(), order.getUser(), order.getItem());

        // Here you'd integrate with an email/SMS provider. For now we just log.
        log.info("Notify user {} about order {} (status={})", order.getUser(), order.getOrderId(), order.getStatus());
    }
}
