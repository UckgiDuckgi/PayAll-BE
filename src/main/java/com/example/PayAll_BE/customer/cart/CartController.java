package com.example.PayAll_BE.customer.cart;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.customer.cart.dto.CartRequestDto;
import com.example.PayAll_BE.customer.cart.dto.UpdateQuantityRequestDto;
import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.global.exception.UnauthorizedException;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Cart", description = "장바구니 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
	private final CartService cartService;
	private final JwtService jwtService;
	private final AuthService authService;

	@Operation(
		summary = "장바구니 상품 추가",
		description = "사용자가 장바구니에 상품을 추가합니다."
	)
	@PostMapping
	public ResponseEntity<ApiResult> addCart(HttpServletRequest request,
		@RequestBody CartRequestDto cartRequestDto) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "장바구니 추가 성공", cartService.addCart(authId, cartRequestDto)));
	}

	@Operation(
		summary = "장바구니 상품 내역 조회",
		description = "사용자가 장바구니에 담긴 상품 내역을 조회합니다."
	)
	@GetMapping
	public ResponseEntity<ApiResult> getCarts(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		return ResponseEntity.ok(new ApiResult(200, "OK", "장바구니 내역 조회 성공", cartService.getCarts(authId)));

	}

	@Operation(
		summary = "장바구니 상품 수량 변경",
		description = "사용자가 장바구니에 물품을 추가합니다."
	)
	@PatchMapping("/{cartId}")
	public ResponseEntity<ApiResult> updateQuantity(HttpServletRequest request,
		@PathVariable Long cartId,
		@RequestBody UpdateQuantityRequestDto requestDto) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		cartService.updateQuantity(cartId, requestDto.getQuantity(), authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "상품 수량이 수정되었습니다.", null));
	}

	@Operation(
		summary = "장바구니 상품 삭제",
		description = "사용자가 장바구니에서 상품을 삭제합니다."
	)
	@DeleteMapping("/{cartId}")
	public ResponseEntity<ApiResult> deleteCart(HttpServletRequest request,
		@PathVariable Long cartId) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		String authId = jwtService.extractAuthId(accessToken);
		cartService.deleteCart(cartId, authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "상품이 장바구니에서 삭제되었습니다.", null));
	}
}
