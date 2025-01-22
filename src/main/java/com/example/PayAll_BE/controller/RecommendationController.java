package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.RecommendationResponseDto;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.repository.UserRepository;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.JwtService;
import com.example.PayAll_BE.service.RecommendationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

	private final RecommendationService recommendationService;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final AuthService authService;

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
}
