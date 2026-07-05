package com.swst41062.orderservice.client;

import com.swst41062.orderservice.dto.ProductDto;
import com.swst41062.orderservice.exception.ProductServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {

    private final RestTemplate restTemplate;
    private final String productServiceBaseUrl;

    public ProductClient(RestTemplate restTemplate,
                          @Value("${services.product-service.base-url}") String productServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.productServiceBaseUrl = productServiceBaseUrl;
    }

    public ProductDto getProductById(Long productId) {
        String url = productServiceBaseUrl + "/api/products/" + productId;
        try {
            return restTemplate.getForObject(url, ProductDto.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductServiceException("Product not found with id: " + productId);
        } catch (RestClientException ex) {
            throw new ProductServiceException("Unable to reach product-service: " + ex.getMessage(), ex);
        }
    }
}
