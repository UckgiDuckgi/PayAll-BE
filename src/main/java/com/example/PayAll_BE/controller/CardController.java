package com.example.PayAll_BE.controller;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Card.CardResponseDto;
import com.example.PayAll_BE.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

	private final CardService cardService;

	@GetMapping
	public ResponseEntity<ApiResult> getAllCards() {
		List<CardResponseDto> cards = cardService.getAllCards();
		return ResponseEntity.ok(new ApiResult(200, "OK", "전체 카드 조회 성공", cards));
	}
}
