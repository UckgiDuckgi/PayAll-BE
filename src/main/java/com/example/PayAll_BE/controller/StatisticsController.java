package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Statistics.StatisticsDetailResponseDto;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.service.StatisticsService;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
	private final StatisticsService statisticsService;

	@GetMapping
	public ResponseEntity<ApiResult> getStatistics(
		@RequestHeader("Authorization") String token,
		@RequestParam String date
	) {
		String jwtToken = token.replace("Bearer ", "");
		statisticsService.setStatistics(token);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "소비분석 조회 성공", statisticsService.getStatistics(jwtToken, date))
		);
	}

	@GetMapping("/{category}")
	public ResponseEntity<ApiResult> getStatisticsDetails(
		@PathVariable Category category,
		@RequestParam Long userId,
		@RequestParam String date
	) {
		StatisticsDetailResponseDto details = statisticsService.getCategoryDetails(userId, category, date);
		return ResponseEntity.ok(new ApiResult(200, "OK", "카테고리별 소비 분석 상세 조회 성공", details));
	}

}
