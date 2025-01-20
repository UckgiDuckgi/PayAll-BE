package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.mydata.service.MydataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MyDataController {
	private final MydataService mydataService;

	@GetMapping("/mydata")
	public ResponseEntity<ApiResult> getTest(@RequestHeader("Authorization") String token) {
		mydataService.syncMydataInfo(token);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "마이데이터 연동 성공")
		);
	}
}
