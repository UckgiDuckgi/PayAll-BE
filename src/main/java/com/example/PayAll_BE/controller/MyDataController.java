package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.mydata.service.MydataService;
import com.example.PayAll_BE.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MyDataController {
	private final MydataService mydataService;
	private final AuthService authService;

	@GetMapping("/mydata")
	public ResponseEntity<ApiResult> getTest(HttpServletRequest httpServletRequest) {
		String token = authService.getCookieValue(httpServletRequest, "accessToken");
		mydataService.syncMydataInfo(token);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "마이데이터 연동 성공")
		);
	}
}
