package com.example.kafkaordersystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Order Model Class
 *
 * Represents an order object that will be sent through Kafka topics.
 * This class provides explicit getters/setters so it does not rely on Lombok.
 */
public class Order {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("user")
    private String user;

    @JsonProperty("item")
    private String item;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("status")
    private String status;

    // No-arg constructor
    public Order() {
    }

    // All-args constructor
    public Order(String orderId, String user, String item, Long timestamp, String status) {
        this.orderId = orderId;
        this.user = user;
        this.item = item;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Convenience constructor for minimal fields
    public Order(String orderId, String user, String item) {
        this.orderId = orderId;
        this.user = user;
        this.item = item;
        this.timestamp = System.currentTimeMillis();
        this.status = "PENDING";
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
            "orderId='" + orderId + '\'' +
            ", user='" + user + '\'' +
            ", item='" + item + '\'' +
            ", timestamp=" + timestamp +
            ", status='" + status + '\'' +
            '}';
    }
}
