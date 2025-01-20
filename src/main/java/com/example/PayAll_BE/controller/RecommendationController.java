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
import com.example.PayAll_BE.service.JwtService;
import com.example.PayAll_BE.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

	private final RecommendationService recommendationService;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	@GetMapping
	public ResponseEntity<?> recommendation(@RequestHeader("Authorization") String token){
		String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));

		List<RecommendationResponseDto> recommendations = recommendationService.getRecommendation(user);

		return ResponseEntity.ok(new ApiResult(200,"OK","추천 데이터 응답 성공", recommendations));
	}

	@GetMapping("set")
	public ResponseEntity<?> setRecommendation(
		@RequestHeader("Authorization") String token,
		@RequestParam String yearMonth) {
		// JWT에서 사용자 인증 정보 추출
		String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));

		// 기간에 맞는 데이터 생성
		recommendationService.generateBenefits(user,yearMonth);

		// 응답 반환
		return ResponseEntity.ok(new ApiResult(200, "OK", "데이터 적재 성공"));
	}
}
