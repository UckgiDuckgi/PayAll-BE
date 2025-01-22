package com.example.PayAll_BE.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.dto.Purchase.PurchaseRequestDto;
import com.example.PayAll_BE.dto.Purchase.TransactionRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {
	private final RestTemplate restTemplate;

	@Value("${server1.base-url}")
	private String baseUrl;

	public void syncMydata(String token, PurchaseRequestDto purchaseRequestDto) {
		TransactionRequestDto transactionRequestDto = TransactionRequestDto.builder()
			.price(purchaseRequestDto.getTotalPrice())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<TransactionRequestDto> entity = new HttpEntity<>(transactionRequestDto, headers);

		String url = baseUrl + "/api/accounts/purchase";

		try {
			restTemplate.postForEntity(url, entity, Void.class);
			log.info("거래 내역 마이데이터에 저장 성공");
		} catch (Exception e) {
			throw new RuntimeException("거래 내역 마이데이터에 저장 실패");
		}
	}
}
