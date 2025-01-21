package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Cart.CartRequestDto;
import com.example.PayAll_BE.dto.Cart.UpdateQuantityRequestDto;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.CartService;
import com.example.PayAll_BE.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
	private final CartService cartService;
	private final JwtService jwtService;
	private final AuthService authService;

	@PostMapping
	public ResponseEntity<ApiResult> addCart(HttpServletRequest request,
		@RequestBody CartRequestDto cartRequestDto) {
		String accessToken = authService.getCookieValue(request, "access_token");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "장바구니 추가 성공", cartService.addCart(authId, cartRequestDto)));
	}

	@GetMapping
	public ResponseEntity<ApiResult> getCarts(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "access_token");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		return ResponseEntity.ok(new ApiResult(200, "OK", "장바구니 내역 조회 성공", cartService.getCarts(authId)));

	}

	@PatchMapping("/{cartId}")
	public ResponseEntity<ApiResult> updateQuantity(HttpServletRequest request,
		@PathVariable Long cartId,
		@RequestBody UpdateQuantityRequestDto requestDto) {
		String accessToken = authService.getCookieValue(request, "access_token");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		cartService.updateQuantity(cartId, requestDto.getQuantity(), authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "상품 수량이 수정되었습니다.", null));
	}

	@DeleteMapping("/{cartId}")
	public ResponseEntity<ApiResult> deleteCart(HttpServletRequest request,
		@PathVariable Long cartId) {
		String accessToken = authService.getCookieValue(request, "access_token");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		cartService.deleteCart(cartId, authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "상품이 장바구니에서 삭제되었습니다.", null));
	}

	@DeleteMapping()
	public ResponseEntity<ApiResult> deleteSelectedCart(HttpServletRequest request,
		@RequestBody List<Long> cartIds) {
		String accessToken = authService.getCookieValue(request, "access_token");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		cartService.deleteCarts(cartIds, authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "선택된 상품이 장바구니에서 삭제되었습니다.", null));
	}
}
