package com.swst41062.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swst41062.orderservice.dto.OrderRequest;
import com.swst41062.orderservice.dto.OrderResponse;
import com.swst41062.orderservice.exception.ProductServiceException;
import com.swst41062.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_validRequest_returns201() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(100L);
        request.setProductId(1L);
        request.setQuantity(2);

        OrderResponse response = new OrderResponse();
        response.setOrderId(5L);
        response.setCustomerId(100L);
        response.setProductId(1L);
        response.setTotalPrice(BigDecimal.valueOf(40));

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(5))
                .andExpect(jsonPath("$.totalPrice").value(40));
    }

    @Test
    void createOrder_missingQuantity_returns400() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(100L);
        request.setProductId(1L);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_productServiceDown_returns502() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(100L);
        request.setProductId(99L);
        request.setQuantity(1);

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new ProductServiceException("Unable to reach product-service"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadGateway());
    }
}
