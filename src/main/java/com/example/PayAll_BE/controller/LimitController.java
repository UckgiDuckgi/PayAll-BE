package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Limit.LimitRegisterRequestDto;
import com.example.PayAll_BE.dto.Limit.LimitRegisterResponseDto;
import com.example.PayAll_BE.dto.Limit.LimitResponseDto;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.JwtService;
import com.example.PayAll_BE.service.LimitService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/limit")
public class LimitController {
	private final LimitService limitService;
	private final AuthService authService;
	private final JwtService jwtService;

	// 소비목표 등록
	@PostMapping
	public ResponseEntity<ApiResult> registerLimit(
		HttpServletRequest request,
		@RequestBody LimitRegisterRequestDto requestDto
	) {
		String token = authService.getCookieValue(request, "accessToken");
		Long userId = jwtService.extractUserId(token);
		LimitRegisterResponseDto responseDto = limitService.registerLimit(userId, requestDto);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "소비 목표 등록 성공", responseDto)
		);
	}

	// 소비목표 조회
	@GetMapping
	public ResponseEntity<ApiResult> getLimit(HttpServletRequest request) {
		String token = authService.getCookieValue(request, "accessToken");
		Long userId = jwtService.extractUserId(token);
		LimitResponseDto responseDto = limitService.getLimit(userId);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "소비 목표 조회 성공", responseDto)
		);
	}
}
