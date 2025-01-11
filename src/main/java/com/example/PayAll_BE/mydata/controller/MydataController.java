package com.example.PayAll_BE.mydata.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.PayAll_BE.mydata.service.MydataService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class MydataController {

	private final MydataService mydataService;
	@GetMapping
	public ResponseEntity<?> getAccountList(
		@RequestHeader("Authorization") String authorization,
		@RequestHeader("x-api-tran-id") String transactionId,
		@RequestHeader("x-api-type") String apiType,
		@RequestHeader("org_code") String orgCode,
		@RequestParam(required = false) String search_timestamp,
		@RequestParam(required = false) String next_page,
		@RequestParam int limit
	) {
		return ResponseEntity.ok(mydataService.getAccountList(authorization, transactionId, apiType, orgCode, search_timestamp, next_page, limit));
	}
}
