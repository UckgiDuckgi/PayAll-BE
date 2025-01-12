package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.CartRequestDto;
import com.example.PayAll_BE.dto.CartResponseDto;
import com.example.PayAll_BE.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
	private final CartService cartService;

	@PostMapping
	public ResponseEntity<CartResponseDto> addCart(@RequestBody CartRequestDto cartRequestDto) {
		return ResponseEntity.ok(cartService.addCart(cartRequestDto));
	}

	@GetMapping
	public ResponseEntity<List<CartResponseDto>> getCarts(@RequestParam Long userId) {  // todo) @RequestParam 제거 필요
		return ResponseEntity.ok(cartService.getCarts(userId));
	}

	@DeleteMapping("/{cartId}")
	public ResponseEntity<ApiResult> deleteCart(@PathVariable Long cartId, @RequestParam Long userId) {
		cartService.deleteCart(cartId, userId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "상품이 장바구니에서 삭제되었습니다."));
	}

	@DeleteMapping()
	public ResponseEntity<ApiResult> deleteSelectedCart(@RequestBody List<Long> cartIds, @RequestParam Long userId) {
		cartService.deleteCarts(cartIds, userId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "선택된 상품이 장바구니에서 삭제되었습니다."));
	}
}
