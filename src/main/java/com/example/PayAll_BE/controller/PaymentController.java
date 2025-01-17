package com.example.PayAll_BE.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Payment.PaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentUpdateRequest;
import com.example.PayAll_BE.dto.Payment.TotalPaymentResponseDto;
import com.example.PayAll_BE.dto.PaymentDetail.PaymentDetailInfoRequestDto;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping
	public ResponseEntity<ApiResult> getPayments(
		@RequestHeader("Authorization") String token,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) Long accountId,
		Pageable pageable
	) {
		TotalPaymentResponseDto response = paymentService.getPayments(token, accountId, category, pageable);
		return ResponseEntity.ok(new ApiResult(200, "OK", "통합 계좌 거래 내역 조회 성공", response));
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<ApiResult> getPaymentDetail(
		@RequestHeader("Authorization") String token,
		@PathVariable Long paymentId
	) {
		PaymentResponseDto paymentResponseDto = paymentService.getPaymentById(paymentId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "결제 상세 조회 성공", paymentResponseDto));
	}

	@PostMapping("/details")
	public ResponseEntity<ApiResult> uploadPaymentDetail(
		@RequestHeader("Authorization") String token,
		@RequestBody PaymentDetailInfoRequestDto requestDto) {
		paymentService.uploadPaymentDetail(token, requestDto);
		return ResponseEntity.ok(new ApiResult(200,"OK", "결제 내역 상세 업로드 성공", null));
	}

	@PatchMapping
	public ResponseEntity<ApiResult> uploadPayments(@RequestBody PaymentUpdateRequest paymentRequest) {
		try {
			paymentService.updatePaymentPlaces(paymentRequest.getPaymentList());
			return ResponseEntity.ok(new ApiResult(200, "OK", "결제처 업데이트가 완료되었습니다."));
		} catch (NotFoundException e) {
			return ResponseEntity.status(404).body(new ApiResult(404, "NOT_FOUND", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ApiResult(500, "ERROR", "결제처 업데이트 중 오류가 발생했습니다."));
		}
	}
}
