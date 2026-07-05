package com.swst41062.orderservice.service;

import com.swst41062.orderservice.client.ProductClient;
import com.swst41062.orderservice.dto.OrderEventMessage;
import com.swst41062.orderservice.dto.OrderRequest;
import com.swst41062.orderservice.dto.OrderResponse;
import com.swst41062.orderservice.dto.ProductDto;
import com.swst41062.orderservice.exception.ProductServiceException;
import com.swst41062.orderservice.messaging.OrderEventPublisher;
import com.swst41062.orderservice.model.Order;
import com.swst41062.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest request;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        request = new OrderRequest();
        request.setCustomerId(100L);
        request.setProductId(1L);
        request.setQuantity(3);

        productDto = new ProductDto();
        productDto.setProductId(1L);
        productDto.setName("Wireless Mouse");
        productDto.setUnitPrice(BigDecimal.valueOf(20));
    }

    @Test
    void createOrder_validRequest_computesTotalSavesAndPublishesEvent() {
        when(productClient.getProductById(1L)).thenReturn(productDto);

        Order savedOrder = new Order(10L, 100L, 1L, "Wireless Mouse", 3, BigDecimal.valueOf(60), Instant.now(), "CREATED");
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.getOrderId()).isEqualTo(10L);
        assertThat(response.getTotalPrice()).isEqualByComparingTo("60");
        assertThat(response.getProductName()).isEqualTo("Wireless Mouse");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getTotalPrice()).isEqualByComparingTo("60");

        ArgumentCaptor<OrderEventMessage> eventCaptor = ArgumentCaptor.forClass(OrderEventMessage.class);
        verify(orderEventPublisher).publishOrderCreated(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getOrderId()).isEqualTo(10L);
        assertThat(eventCaptor.getValue().getCustomerId()).isEqualTo(100L);
    }

    @Test
    void createOrder_productServiceUnavailable_throwsAndDoesNotSaveOrPublish() {
        when(productClient.getProductById(1L)).thenThrow(new ProductServiceException("Unable to reach product-service"));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ProductServiceException.class);

        verify(orderRepository, never()).save(any());
        verify(orderEventPublisher, never()).publishOrderCreated(any());
    }
}
