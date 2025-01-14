package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Payment.PaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.TotalPaymentResponseDto;
import com.example.PayAll_BE.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping
	public ResponseEntity<ApiResult> getPayments(@RequestParam Long userId ) { //authentication으로 바꿔야 함
		return ResponseEntity.ok(new ApiResult(200, "OK", "통합 계좌 거래 내역 조회 성공", paymentService.getPayments(userId)));
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<ApiResult> getPaymentDetail(@PathVariable Long paymentId) {
		PaymentResponseDto paymentResponseDto = paymentService.getPaymentById(paymentId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "결제 상세 조회 성공", paymentResponseDto));
	}
}
