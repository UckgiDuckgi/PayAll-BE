package com.example.PayAll_BE.mydata.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.PayAll_BE.mydata.dto.AccountListResponseDto;
import com.example.PayAll_BE.mydata.dto.AccountRequestDto;
import com.example.PayAll_BE.mydata.dto.AccountResponseDto;
import com.example.PayAll_BE.mydata.dto.TransactionRequestDto;
import com.example.PayAll_BE.mydata.dto.TransactionResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class MydataController {

	private final RestTemplate restTemplate;

	@Value("${server1.base-url}")
	private String server1BaseUrl;

	@GetMapping("/load")
	public ResponseEntity<AccountListResponseDto> loadMydataAccountList() {
		// base URL과 endpoint
		String url = server1BaseUrl + "api/accounts";

		// 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer my-test-token"); // 토큰 설정
		headers.set("x-api-tran-id", "12345");
		headers.set("x-api-type", "REGULAR");
		headers.set("org_code", "98765");

		// limit 쿼리 파라미터 추가
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
			.queryParam("next_page", 1)
			.queryParam("limit", 3); // limit 값 설정

		// HttpEntity 생성
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<AccountListResponseDto> response = restTemplate.exchange(
			uriBuilder.toUriString(), // 최종 URL
			HttpMethod.GET,
			entity,
			AccountListResponseDto.class
		);

		return ResponseEntity.ok(response.getBody());
	}

	@PostMapping("/basic")
	public ResponseEntity<AccountResponseDto> getAccountBasicInfo(
		@RequestBody AccountRequestDto requestDto) {

		// 외부 API URL
		String url = server1BaseUrl + "api"
			+ "/accounts/basic";

		// 요청 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer my-test-token"); // 토큰 설정
		headers.set("x-api-tran-id", "12345");
		headers.set("x-api-type", "REGULAR");

		HttpEntity<AccountRequestDto> entity = new HttpEntity<>(requestDto, headers);

		ResponseEntity<AccountResponseDto> response = restTemplate.exchange(
			url,
			HttpMethod.POST,
			entity,
			AccountResponseDto.class
		);

		return ResponseEntity.ok(response.getBody());
	}

	@PostMapping("/transactions")
	public ResponseEntity<TransactionResponseDto> getAccountTransactions(
		@RequestBody TransactionRequestDto requestDto) {
		String url = server1BaseUrl + "/api/accounts/transactions";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer my-test-token"); // 적절한 토큰 설정
		headers.set("x-api-tran-id", "12345");
		headers.set("x-api-type", "REGULAR");

		HttpEntity<TransactionRequestDto> entity = new HttpEntity<>(requestDto, headers);

		ResponseEntity<TransactionResponseDto> response = restTemplate.exchange(
			url,
			HttpMethod.POST,
			entity,
			TransactionResponseDto.class
		);

		return ResponseEntity.ok(response.getBody());
	}
}
