package com.example.paymentservice.model;

import java.time.Instant;

public class Order {
    private String orderId;
    private String user;
    private String item;
    private String status;
    private Instant timestamp;

    public Order() {
    }

    public Order(String orderId, String user, String item, String status, Instant timestamp) {
        this.orderId = orderId;
        this.user = user;
        this.item = item;
        this.status = status;
        this.timestamp = timestamp;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
