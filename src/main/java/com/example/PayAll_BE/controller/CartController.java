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
import com.example.PayAll_BE.service.CartService;
import com.example.PayAll_BE.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
	private final CartService cartService;
	private final JwtService jwtService;

	@PostMapping
	public ResponseEntity<ApiResult> addCart(@RequestHeader("Authorization") String token,
		@RequestBody CartRequestDto cartRequestDto) {
		String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "장바구니 추가 성공", cartService.addCart(authId, cartRequestDto)));
	}

	@GetMapping
	public ResponseEntity<ApiResult> getCarts(@RequestHeader("Authorization") String token) {
		String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		return ResponseEntity.ok(new ApiResult(200, "OK", "장바구니 내역 조회 성공", cartService.getCarts(authId)));

	}

	@PatchMapping("/{cartId}")
	public ResponseEntity<ApiResult> updateQuantity(@RequestHeader("Authorization") String token,
		@PathVariable Long cartId,
		@RequestBody UpdateQuantityRequestDto requestDto) {
		String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		cartService.updateQuantity(cartId, requestDto.getQuantity(), authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "상품 수량이 수정되었습니다.", null));
	}

	@DeleteMapping("/{cartId}")
	public ResponseEntity<ApiResult> deleteCart(@RequestHeader("Authorization") String token,
		@PathVariable Long cartId) {
		String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		cartService.deleteCart(cartId, authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "상품이 장바구니에서 삭제되었습니다.", null));
	}

	@DeleteMapping()
	public ResponseEntity<ApiResult> deleteSelectedCart(@RequestHeader("Authorization") String token,
		@RequestBody List<Long> cartIds) {
		String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		cartService.deleteCarts(cartIds, authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "선택된 상품이 장바구니에서 삭제되었습니다.", null));
	}
}
