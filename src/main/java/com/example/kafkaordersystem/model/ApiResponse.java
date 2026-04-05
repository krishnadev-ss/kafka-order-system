package com.example.kafkaordersystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API Response wrapper class for consistent API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /**
     * Response status: "success" or "error"
     */
    @JsonProperty("status")
    private String status;

    /**
     * Response message
     */
    @JsonProperty("message")
    private String message;

    /**
     * Response data (generic type)
     */
    @JsonProperty("data")
    private T data;

    /**
     * Timestamp when response was generated
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * Constructor for creating a success response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .status("success")
            .message(message)
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }

    /**
     * Constructor for creating an error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .status("error")
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
