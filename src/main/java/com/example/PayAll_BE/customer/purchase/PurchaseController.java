package com.example.PayAll_BE.customer.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.global.exception.UnauthorizedException;
import com.example.PayAll_BE.global.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Purchase", description = "상품 구매 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {
	private final AuthService authService;
	private final PurchaseService purchaseService;

	@Operation(
		summary = "상품 구매",
		description = "사용자가 상품을 구매합니다."
	)
	@PostMapping
	public ResponseEntity<ApiResult> addCart(HttpServletRequest request,
		@RequestBody PurchaseRequestDto purchaseRequestDto) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if (accessToken == null) {
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}

		purchaseService.syncMydata(accessToken, purchaseRequestDto);

		return ResponseEntity.ok(
			new ApiResult(200, "OK", "구매 성공"));
	}
}
