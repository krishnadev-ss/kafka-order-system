package com.example.kafkaordersystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order Model Class
 * 
 * Represents an order object that will be sent through Kafka topics.
 * Uses Lombok annotations to reduce boilerplate code:
 * - @Data: generates getters, setters, toString, equals, hashCode
 * - @NoArgsConstructor: generates constructor with no arguments
 * - @AllArgsConstructor: generates constructor with all fields as arguments
 * - @Builder: provides builder pattern for object creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {

    /**
     * Unique identifier for the order
     */
    @JsonProperty("orderId")
    private String orderId;

    /**
     * Username or customer identifier
     */
    @JsonProperty("user")
    private String user;

    /**
     * Item description or product name
     */
    @JsonProperty("item")
    private String item;

    /**
     * Timestamp when the order was created (optional)
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * Status of the order (optional)
     */
    @JsonProperty("status")
    private String status;

    /**
     * Constructor for creating an Order with essential fields
     */
    public Order(String orderId, String user, String item) {
        this.orderId = orderId;
        this.user = user;
        this.item = item;
        this.timestamp = System.currentTimeMillis();
        this.status = "PENDING";
    }
}
