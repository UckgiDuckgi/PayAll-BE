package com.example.PayAll_BE.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.AuthRequestDto;
import com.example.PayAll_BE.dto.AuthResponseDto;
import com.example.PayAll_BE.dto.RefreshTokenRequestDto;
import com.example.PayAll_BE.dto.RegisterRequestDto;
import com.example.PayAll_BE.exception.BadRequestException;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.JwtService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cert")
public class AuthController {
	private final AuthService authService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequestDto request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResult> register(@RequestBody RegisterRequestDto request) {
		if (request.getPassword() == null || request.getPassword().isEmpty()) {
			throw new BadRequestException("올바른 비밀번호를 입력해주세요.");
		}

		ApiResult response = authService.register(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request) {
			AuthResponseDto newTokens = authService.refreshToken(request.getRefreshToken());
			return ResponseEntity.ok(new ApiResult(200, "OK", "토큰 갱신 성공", newTokens));
	}

	@GetMapping("/test")
	public String test(@RequestHeader("Authorization") String token){
		String userId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		System.out.println("userId = " + userId);
		return userId;
	}
}
