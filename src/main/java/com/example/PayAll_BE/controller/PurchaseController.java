package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Purchase.PurchaseRequestDto;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.PurchaseService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {
	private final AuthService authService;
	private final PurchaseService purchaseService;

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
