package com.example.PayAll_BE.global.mydata.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.PayAll_BE.global.mydata.dto.AccountListResponseDto;
import com.example.PayAll_BE.global.mydata.dto.AccountRequestDto;
import com.example.PayAll_BE.global.mydata.dto.AccountResponseDto;
import com.example.PayAll_BE.global.mydata.dto.TransactionRequestDto;
import com.example.PayAll_BE.global.mydata.dto.TransactionResponseDto;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MyDataAccount", description = "MyData 관련 계좌 및 거래 정보 관리")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mydata")
public class MydataController {

	private final RestTemplate restTemplate;
	private final JwtService jwtService;
	private final AuthService authService;

	@Value("${server1.base-url}")
	private String server1BaseUrl;

	@Operation(
		summary = "사용자 계좌 목록 로드",
		description = "Mydata를 통해 사용자의 계좌 목록을 불러옵니다."
	)
	@GetMapping("/load")
	public ResponseEntity<AccountListResponseDto> loadMydataAccountList(String token) {
		String url = server1BaseUrl + "api/accounts";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		headers.set("x-api-tran-id", "12345");
		headers.set("x-api-type", "REGULAR");
		headers.set("org_code", "98765");

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
			// .queryParam("next_page", 1)
			// .queryParam("search_timestamp", "0")
			.queryParam("limit", 3);

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<AccountListResponseDto> response = restTemplate.exchange(
			uriBuilder.toUriString(),
			HttpMethod.GET,
			entity,
			AccountListResponseDto.class
		);

		return ResponseEntity.ok(response.getBody());
	}

	@Operation(
		summary = "사용자 계좌 정보 조회 및 등록",
		description = "Mydata를 통해 사용자의 계좌 정보들을 불러와 등록합니다."
	)
	@PostMapping("/basic")
	public ResponseEntity<AccountResponseDto> getAccountBasicInfo(
		@RequestBody AccountRequestDto requestDto) {

		String url = server1BaseUrl + "api"
			+ "/accounts/basic";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer my-test-token");
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

	@Operation(
		summary = "사용자 계좌 거래 내역 조회 및 등록",
		description = "Mydata를 통해 사용자의 계좌 거래내역을 불러와 등록합니다."
	)
	@PostMapping("/transactions")
	public ResponseEntity<TransactionResponseDto> getAccountTransactions(
		@RequestBody TransactionRequestDto requestDto) {
		String url = server1BaseUrl + "/api/accounts/transactions";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer my-test-token");
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
