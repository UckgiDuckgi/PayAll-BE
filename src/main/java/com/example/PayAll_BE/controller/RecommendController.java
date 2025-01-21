package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.JwtService;
import com.example.PayAll_BE.service.RecommendService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendController {
	private final RecommendService recommendService;
	private final JwtService jwtService;
	private final AuthService authService;

	@GetMapping("/products")
	public ResponseEntity<ApiResult> getRecommendProducts(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "access_token");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "최근 지출 품목 최저가 추천 성공", recommendService.getRecommendProducts(authId)));
	}
}
