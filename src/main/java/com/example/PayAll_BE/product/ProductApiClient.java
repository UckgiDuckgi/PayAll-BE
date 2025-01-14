package com.example.PayAll_BE.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProductApiClient {
	@Value("${server1.base-url}")
	private String baseUrl;

	private final RestTemplate restTemplate;

	public ProductDto fetchProduct(String productId) {
		String productApiUrl = baseUrl + "redis/product/";
		ResponseEntity<ProductDto> response = restTemplate.getForEntity(
			productApiUrl + productId, ProductDto.class);

		if (response.getBody() == null) {
			throw new NotFoundException("상품 id가 없습니다.");
		}

		return response.getBody();
	}
}
