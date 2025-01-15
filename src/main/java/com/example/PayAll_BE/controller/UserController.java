package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.UserResponseDto;
import com.example.PayAll_BE.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping
	public ResponseEntity<ApiResult> getUserInfo(
		@RequestParam Long userId
	) {
		UserResponseDto userInfo = userService.getUserInfo(userId);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "사용자 정보 조회 성공", userInfo)
		);
	}
}
