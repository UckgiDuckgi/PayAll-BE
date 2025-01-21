package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.ProductResponseDto;
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
@RequestMapping("/api/product")
public class ProductController {
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final RecommendationService recommendationService;
	private final AuthService authService;

	@GetMapping("{productId}")
	public ResponseEntity<?> calculateBenefit(HttpServletRequest request,@PathVariable("productId") Long productId){
		String accessToken = authService.getCookieValue(request, "access_token");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));
		List<ProductResponseDto> discountResult = recommendationService.calculateDiscount(user,productId);

		return ResponseEntity.ok(new ApiResult(200,"OK","추천 데이터 응답 성공", discountResult));
	}
}
