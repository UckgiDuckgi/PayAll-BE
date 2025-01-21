package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.UserResponseDto;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.exception.UnauthorizedException;
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
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		UserResponseDto userInfo = userService.getUserInfo(accessToken);

		return ResponseEntity.ok(new ApiResult(200, "OK", "사용자 정보 조회 성공", userInfo));
	}
}
