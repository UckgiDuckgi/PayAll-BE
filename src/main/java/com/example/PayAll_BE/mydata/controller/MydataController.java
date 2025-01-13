package com.example.PayAll_BE.mydata.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.PayAll_BE.dto.AccountResponseDto;
import com.example.PayAll_BE.dto.GetAccountsDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class MydataController {

	private final RestTemplate restTemplate;

	@Value("${server1.base-url}")
	private String server1BaseUrl;

	@GetMapping("/load")
	public ResponseEntity<AccountResponseDto> loadMydataAccount() {
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
			.queryParam("next_page",1)
			.queryParam("limit", 3); // limit 값 설정

		// HttpEntity 생성
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<AccountResponseDto> response = restTemplate.exchange(
			uriBuilder.toUriString(), // 최종 URL
			HttpMethod.GET,
			entity,
			AccountResponseDto.class
		);

		return ResponseEntity.ok(response.getBody());
	}
}
