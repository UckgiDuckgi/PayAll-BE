package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Statistics.StatisticsDetailResponseDto;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.StatisticsService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
	private final StatisticsService statisticsService;
	private final AuthService authService;
	private final JwtService jwtService;

	@GetMapping
	public ResponseEntity<ApiResult> getStatistics(
		HttpServletRequest request,
		@RequestParam String date
	) {
		String token = authService.getCookieValue(request, "accessToken");
		statisticsService.setStatistics(token);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "소비분석 조회 성공", statisticsService.getStatistics(token, date))

		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "소비분석 조회 성공", statisticsService.getStatistics(accessToken, date))
		);
	}

	@GetMapping("/{category}")
	public ResponseEntity<ApiResult> getStatisticsDetails(
		HttpServletRequest request,
		@PathVariable Category category,
		@RequestParam String date
	) {
		String token = authService.getCookieValue(request, "accessToken");
		Long userId = jwtService.extractUserId(token);
		StatisticsDetailResponseDto details = statisticsService.getCategoryDetails(userId, category, date);
		return ResponseEntity.ok(new ApiResult(200, "OK", "카테고리별 소비 분석 상세 조회 성공", details));
	}

}
