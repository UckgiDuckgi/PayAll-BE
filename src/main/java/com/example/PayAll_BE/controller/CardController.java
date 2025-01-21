package com.example.PayAll_BE.controller;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Card.CardResponseDto;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.CardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

	private final CardService cardService;
	private final AuthService authService;

	@GetMapping
	public ResponseEntity<ApiResult> getAllCards(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		List<CardResponseDto> cards = cardService.getAllCards();
		return ResponseEntity.ok(new ApiResult(200, "OK", "전체 카드 조회 성공", cards));
	}
}
