package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.CardRecommendationResultDto;
import com.example.PayAll_BE.entity.User;
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
	public ResponseEntity<?> recommend(@RequestHeader("Authorization") String token) {
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId);
		recommendationService.generateBenefits(user);
		return ResponseEntity.ok((new ApiResult(200,"OK","데이터 적재")));
	}
}
