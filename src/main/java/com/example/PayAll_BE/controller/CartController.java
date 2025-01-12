package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
