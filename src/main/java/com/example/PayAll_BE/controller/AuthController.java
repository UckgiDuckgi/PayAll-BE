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
import com.example.PayAll_BE.dto.RegisterRequestDto;
import com.example.PayAll_BE.service.AuthService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cert")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<ApiResult> register(@RequestBody RegisterRequestDto request) {
		try {
			if (request.getPassword() == null || request.getPassword().isEmpty()) {
				return ResponseEntity.badRequest().body(
					new ApiResult(400, "BAD_REQUEST", "간편 비밀번호를 입력해주세요.", null));
			}

			ApiResult response = authService.register(request);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
				new ApiResult(400, "BAD_REQUEST", "회원가입에 실패했습니다: " + e.getMessage(), null));
		}
	}
}
