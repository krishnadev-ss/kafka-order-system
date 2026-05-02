package com.example.inventoryservice.consumer;

import com.example.inventoryservice.entity.InventoryRecord;
import com.example.inventoryservice.model.Order;
import com.example.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);
    private final InventoryRepository repository;

    public InventoryConsumer(InventoryRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "orders", groupId = "inventory-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(Order order) {
        log.info("InventoryService received order: {}", order.getOrderId());

        // Simulate inventory reservation
        InventoryRecord rec = new InventoryRecord(
                order.getOrderId(),
                order.getItem(),
                1,
                "RESERVED",
                Instant.now());

        repository.save(rec);
        log.info("Inventory reserved for order: {}", order.getOrderId());
    }
}
