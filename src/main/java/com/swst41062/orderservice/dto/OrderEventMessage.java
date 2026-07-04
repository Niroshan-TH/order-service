package com.swst41062.orderservice.dto;

import java.io.Serializable;
import java.time.Instant;

public class OrderEventMessage implements Serializable {

    private Long orderId;
    private Long customerId;
    private Instant timestamp;
    private String message;

    public OrderEventMessage() {
    }

    public OrderEventMessage(Long orderId, Long customerId, Instant timestamp, String message) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.timestamp = timestamp;
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
