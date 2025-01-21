package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
// import com.example.PayAll_BE.dto.Subscription.SubscriptionRecommendResponseDto;
// import com.example.PayAll_BE.dto.Subscription.SubscriptionRequestDto;
import com.example.PayAll_BE.dto.Subscription.SubscriptionResponseDto;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.SubscriptionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
	private final SubscriptionService subscriptionService;
	private final AuthService authService;
	@GetMapping
	public ResponseEntity<ApiResult> getAllSubscriptions(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "access_token");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		List<SubscriptionResponseDto> subscriptions = subscriptionService.getAllSubscriptions();
		return ResponseEntity.ok(new ApiResult(200, "OK", "전체 구독 조회 성공", subscriptions));
	}

	// @GetMapping
	// public ResponseEntity<ApiResult> getRecommendations(
	// 	@RequestHeader("Authorization") String token,
	// 	@RequestBody SubscriptionRequestDto requestDto
	// ) {
	// 	List<SubscriptionRecommendResponseDto> recommendations = subscriptionService.getRecommendations(token, requestDto.getPayments());
	//
	// 	return ResponseEntity.ok(new ApiResult(200, "OK", "추천 구독 정보 조회 성공", recommendations));
	// }

}
