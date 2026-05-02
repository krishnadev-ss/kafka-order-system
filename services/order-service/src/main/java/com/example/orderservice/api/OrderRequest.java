package com.example.orderservice.api;

public class OrderRequest {
    private String orderId;
    private String user;
    private String item;

    public OrderRequest() {
    }

    public OrderRequest(String orderId, String user, String item) {
        this.orderId = orderId;
        this.user = user;
        this.item = item;
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
}

