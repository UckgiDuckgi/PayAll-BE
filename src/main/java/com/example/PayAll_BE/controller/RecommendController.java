package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.service.RecommendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendController {
	private final RecommendService recommendService;

	@GetMapping("/products")
	public ResponseEntity<ApiResult> getRecommendProducts(@RequestParam Long userId) {  // todo. user Id 제거 필요
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "최근 지출 품목 최저가 추천 성공", recommendService.getRecommendProducts(userId)));
	}
}
