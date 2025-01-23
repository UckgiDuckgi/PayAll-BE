package com.example.PayAll_BE.customer.statistics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.customer.statistics.dto.StaticsDiffResponseDto;
import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.customer.statistics.dto.StatisticsDetailResponseDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.global.exception.NotFoundException;
import com.example.PayAll_BE.global.exception.UnauthorizedException;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.customer.recommendation.RecommendationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Statistics", description = "소비 분석 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
	private final StatisticsService statisticsService;
	private final AuthService authService;
	private final JwtService jwtService;
	private final RecommendationService recommendationService;
	private final UserRepository userRepository;

	@Operation(
		summary = "소비 분석 조회",
		description = "사용자의 소비 분석 및 통계를 조회합니다."
	)
	@GetMapping
	public ResponseEntity<ApiResult> getStatistics(
		HttpServletRequest request,
		@RequestParam String date
	) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		String authId = jwtService.extractAuthId(accessToken);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
		statisticsService.setStatistics(user);
		recommendationService.flagSetRecommendation(user);

		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "소비분석 조회 성공", statisticsService.getStatistics(accessToken, date))
		);
	}

	@Operation(
		summary = "카테고리별 소비 분석 상세 조회",
		description = "사용자 카테고리별 소비 분석 상세 내역을 조회합니다."
	)
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

	@GetMapping("/diff")
	public ResponseEntity<ApiResult> getStatisticsDiff(HttpServletRequest request) {
		String token = authService.getCookieValue(request, "accessToken");
		Long userId = jwtService.extractUserId(token);
		StaticsDiffResponseDto responseDto = statisticsService.getDiffStatistics(userId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "금액 차이 조회 성공", responseDto));
	}

}
