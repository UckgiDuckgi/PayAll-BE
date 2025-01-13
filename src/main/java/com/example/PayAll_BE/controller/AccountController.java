package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.Account.AccountListResponseDto;
import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
	private final AccountService accountService;

	@GetMapping
	public ResponseEntity<ApiResult> getAccounts(
		@RequestParam Long userId //authentication으로 바꿔야 함
	) {
		return ResponseEntity.ok(new ApiResult(200, "OK", "사용자 계좌 목록 조회 성공", accountService.getUserAccounts(userId)));
	}
}
