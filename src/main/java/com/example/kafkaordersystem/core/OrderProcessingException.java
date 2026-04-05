package com.example.kafkaordersystem.core;

/**
 * Custom Exception for Order Processing Errors
 */
public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
