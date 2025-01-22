package com.example.PayAll_BE.mydata.controller;

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

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.mydata.dto.AccountListResponseDto;
import com.example.PayAll_BE.mydata.dto.AccountRequestDto;
import com.example.PayAll_BE.mydata.dto.AccountResponseDto;
import com.example.PayAll_BE.mydata.dto.TransactionRequestDto;
import com.example.PayAll_BE.mydata.dto.TransactionResponseDto;
import com.example.PayAll_BE.mydata.service.MydataService;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mydata")
public class MydataController {

	private final RestTemplate restTemplate;
	private final JwtService jwtService;
	private final AuthService authService;
	private final MydataService mydataService;

	@Value("${server1.base-url}")
	private String server1BaseUrl;

	@GetMapping
	public ResponseEntity<ApiResult> getTest(HttpServletRequest httpServletRequest) {
		String token = authService.getCookieValue(httpServletRequest, "accessToken");
		mydataService.syncMydataInfo(token);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "마이데이터 연동 성공")
		);
	}
	@GetMapping("/load")
	public ResponseEntity<AccountListResponseDto> loadMydataAccountList(@RequestHeader("Authorization") String token) {
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
