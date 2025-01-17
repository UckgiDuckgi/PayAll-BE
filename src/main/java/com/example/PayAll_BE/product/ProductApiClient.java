package com.example.PayAll_BE.product;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.dto.SearchProductDto;
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
	public ProductDto fetchProductByName(String productName) {
		String url = baseUrl + "redis/product/by-name/" + productName;
		ResponseEntity<ProductDto> response = restTemplate.getForEntity(url, ProductDto.class);

		if (response.getBody() == null) {
			throw new NotFoundException("해당 상품 이름을 가진 상품이 없습니다.");
		}

		return response.getBody();
	}

	public List<SearchProductDto> searchProducts(String query, int page, int size) {

		String searchApiUrl = String.format("%s/redis/search?page=%d&size=%d&query=%s", baseUrl, page, size,
			URLEncoder.encode(query, StandardCharsets.UTF_8));

		ResponseEntity<SearchProductDto[]> response = restTemplate.getForEntity(
			searchApiUrl, SearchProductDto[].class);

		if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
			return Arrays.asList(response.getBody());
		}

		return Collections.emptyList();
	}
}
