package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.CardRecommendationResultDto;
import com.example.PayAll_BE.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

	private final RecommendationService recommendationService;

	@GetMapping("/cards")
	public ResponseEntity<?> recommendCards(@RequestParam Long accountId) {
		// // 서비스 로직 호출
		// List<CardRecommendationResultDto> recommendations = recommendationService.getCardRecommendations(accountId);
		// return ResponseEntity.ok(new ApiResult(200,"OK","추천 성공", recommendations));
		return null;
	}
}
