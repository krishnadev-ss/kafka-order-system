package com.example.paymentservice.consumer;

import com.example.paymentservice.entity.PaymentRecord;
import com.example.paymentservice.model.Order;
import com.example.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);
    private final PaymentRepository repository;

    public PaymentConsumer(PaymentRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "orders", groupId = "payment-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(Order order) {
        log.info("PaymentService received order: {}", order.getOrderId());

        // Simulate payment processing
        PaymentRecord record = new PaymentRecord(
                order.getOrderId(),
                order.getUser(),
                order.getItem(),
                "PAID",
                Instant.now());

        repository.save(record);
        log.info("Payment processed and saved for order: {}", order.getOrderId());
    }
}
