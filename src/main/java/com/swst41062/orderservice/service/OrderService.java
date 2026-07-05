package com.swst41062.orderservice.service;

import com.swst41062.orderservice.dto.OrderRequest;
import com.swst41062.orderservice.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);
}
