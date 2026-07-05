package com.swst41062.orderservice.service;

import com.swst41062.orderservice.client.ProductClient;
import com.swst41062.orderservice.dto.OrderEventMessage;
import com.swst41062.orderservice.dto.OrderRequest;
import com.swst41062.orderservice.dto.OrderResponse;
import com.swst41062.orderservice.dto.ProductDto;
import com.swst41062.orderservice.messaging.OrderEventPublisher;
import com.swst41062.orderservice.model.Order;
import com.swst41062.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher orderEventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository, ProductClient productClient,
                             OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        ProductDto product = productClient.getProductById(request.getProductId());

        BigDecimal totalPrice = product.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = new Order(
                null,
                request.getCustomerId(),
                product.getProductId(),
                product.getName(),
                request.getQuantity(),
                totalPrice,
                Instant.now(),
                "CREATED"
        );

        Order saved = orderRepository.save(order);

        OrderEventMessage event = new OrderEventMessage(
                saved.getOrderId(),
                saved.getCustomerId(),
                Instant.now(),
                "Order " + saved.getOrderId() + " created for customer " + saved.getCustomerId()
        );
        orderEventPublisher.publishOrderCreated(event);

        return OrderResponse.fromEntity(saved);
    }
}
