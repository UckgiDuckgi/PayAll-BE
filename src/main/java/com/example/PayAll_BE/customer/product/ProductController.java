package com.example.PayAll_BE.customer.product;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.customer.product.dto.ProductDescriptionDto;
import com.example.PayAll_BE.customer.product.dto.ProductResponseDto;
import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.global.exception.UnauthorizedException;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.customer.recommendation.RecommendationService;

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
	private final ProductService productService;

	@GetMapping("{productId}")
	public ResponseEntity<?> calculateBenefit(HttpServletRequest request,@PathVariable("productId") Long productId){
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));
		ProductResponseDto discountResult = recommendationService.calculateDiscount(user,productId);

		return ResponseEntity.ok(new ApiResult(200,"OK","추천 데이터 응답 성공", discountResult));
	}

	@GetMapping("/cards")
	public ResponseEntity<ApiResult> getAllCards(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		List<ProductDescriptionDto> cards = productService.getAllCards();
		return ResponseEntity.ok(new ApiResult(200, "OK", "전체 카드 조회 성공", cards));
	}

	@GetMapping("/subscribes")
	public ResponseEntity<ApiResult> getAllSubscribes(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		List<ProductDescriptionDto> subscribes = productService.getAllSubscriptions();
		return ResponseEntity.ok(new ApiResult(200, "OK", "전체 구독 조회 성공", subscribes));
	}
}
