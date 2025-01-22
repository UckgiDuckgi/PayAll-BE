package com.example.PayAll_BE.customer.recommendation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.customer.recommendation.dto.RecommendationResponseDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.global.exception.UnauthorizedException;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Recommendation", description = "추천 상품 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

	private final RecommendationService recommendationService;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final AuthService authService;

	@Operation(
		summary = "사용자 소비내역 기반 상품 추천",
		description = "사용자의 소비 내역을 바탕으로 사용자에게 적합한 소비 혜택 상품을 추천합니다."
	)
	@GetMapping
	public ResponseEntity<?> recommendation(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));

		List<RecommendationResponseDto> recommendations = recommendationService.getRecommendation(user);

		return ResponseEntity.ok(new ApiResult(200,"OK","추천 데이터 응답 성공", recommendations));
	}
	@GetMapping("/products")
	public ResponseEntity<ApiResult> getRecommendProducts(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "최근 지출 품목 최저가 추천 성공", recommendationService.getRecommendProducts(authId)));
	}
}
