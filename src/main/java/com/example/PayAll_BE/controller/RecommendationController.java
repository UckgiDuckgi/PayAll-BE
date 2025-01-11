package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.CardRecommendationResultDto;
import com.example.PayAll_BE.service.RecommendationService;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

	private final RecommendationService recommendationService;

	// Constructor Injection
	public RecommendationController(RecommendationService recommendationService) {
		this.recommendationService = recommendationService;
	}

	/**
	 * 사용자별 카드 추천 API
	 * @param userId 사용자 ID
	 * @return 추천 카드 및 예상 할인 결과
	 */
	@GetMapping("/cards")
	public ResponseEntity<List<CardRecommendationResultDto>> recommendCards(@RequestParam Long userId) {
		// 서비스 로직 호출
		List<CardRecommendationResultDto> recommendations = recommendationService.getCardRecommendations(userId);
		return ResponseEntity.ok(recommendations);
	}
}
