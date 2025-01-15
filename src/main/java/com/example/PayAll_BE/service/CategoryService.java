package com.example.PayAll_BE.service;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.PayAll_BE.entity.enums.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
	@Value("${kakao.rest-api-key}")
	private String kakaoApiKey;
	@Value("${kakao.api-url}")
	private String kakaoApiUrl;
	private final RestTemplate restTemplate;

	public Category getCategory(String searchQuery) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + kakaoApiKey);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		URI uri = UriComponentsBuilder.fromUriString(kakaoApiUrl)
			.queryParam("query", searchQuery)
			.queryParam("size", 1)
			.encode()
			.build()
			.toUri();

		try {
			ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);

			if (response.getBody() != null) {
				List<Map<String, Object>> documents = (List<Map<String, Object>>)response.getBody().get("documents");
				if (!documents.isEmpty()) {
					return mappingCategory((String)documents.get(0).get("category_group_name"));
				}
			}

		} catch (Exception e) {
			log.error("Kakao API 호출 중 오류 발생", e);
			throw new RuntimeException("Kakao API 호출 오류: " + e.getMessage());
		}
		return Category.OTHERS;
	}

	private Category mappingCategory(String kakaoCategory) {
		if (kakaoCategory == null || kakaoCategory.isEmpty()) {
			return Category.OTHERS;
		}

		return switch (kakaoCategory) {
			case "대형마트", "편의점" -> Category.SHOPPING;
			case "학교", "학원", "어린이집, 유치원" -> Category.EDUCATION;
			case "숙박" -> Category.LIVING;
			case "주차장", "지하철역", "주유소,충전소" -> Category.TRANSPORT;
			case "은행", "문화시설" -> Category.CULTURE;
			case "음식점" -> Category.RESTAURANT;
			case "카페" -> Category.CAFE;
			case "병원", "약국" -> Category.HEALTH;
			default -> Category.OTHERS;
		};
	}

}
