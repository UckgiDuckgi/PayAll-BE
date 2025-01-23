package com.example.PayAll_BE.global.crawlingProduct;

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
import org.springframework.web.util.UriComponentsBuilder;

import com.example.PayAll_BE.customer.search.SearchProductDto;
import com.example.PayAll_BE.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CrawlingProductApiClient {
	@Value("${server1.base-url}")
	private String baseUrl;

	private final RestTemplate restTemplate;

	public CrawlingProductDto fetchProduct(String productId) {
		String productApiUrl = baseUrl + "redis/product/";
		ResponseEntity<CrawlingProductDto> response = restTemplate.getForEntity(
			productApiUrl + productId, CrawlingProductDto.class);

		if (response.getBody() == null) {
			throw new NotFoundException("상품 id가 없습니다.");
		}

		return response.getBody();
	}

	public CrawlingProductDto fetchProductByName(String productName) {
		String url = baseUrl + "redis/product/by-name/" + productName;
		ResponseEntity<CrawlingProductDto> response = restTemplate.getForEntity(url, CrawlingProductDto.class);

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

	public CrawlingProductDto requestCrawling(String productId) {
		String productApiUrl = baseUrl + "/redis/product";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(productApiUrl)
			.queryParam("pcode", productId);

		ResponseEntity<CrawlingProductDto> response = restTemplate.getForEntity(
			builder.toUriString(), CrawlingProductDto.class);

		if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
			return response.getBody();
		} else {
			throw new RuntimeException("크롤링 요청 중 오류 발생");
		}

	}
}
