package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.UserResponseDto;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.JwtService;
import com.example.PayAll_BE.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final AuthService authService;

	@GetMapping
	public ResponseEntity<ApiResult> getUserInfo(HttpServletRequest request) {
		String token = authService.getCookieValue(request, "accessToken");
		UserResponseDto userInfo = userService.getUserInfo(token);

		return ResponseEntity.ok(new ApiResult(200, "OK", "사용자 정보 조회 성공", userInfo));
	}
}
