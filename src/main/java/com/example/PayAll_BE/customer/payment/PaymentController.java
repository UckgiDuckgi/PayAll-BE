package com.example.PayAll_BE.customer.payment;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.customer.payment.dto.PaymentUpdateRequest;
import com.example.PayAll_BE.customer.payment.dto.TotalPaymentResponseDto;
import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.customer.payment.dto.PaymentResponseDto;
import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentListRequestDto;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.global.exception.NotFoundException;
import com.example.PayAll_BE.global.exception.UnauthorizedException;
import com.example.PayAll_BE.global.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment", description = "소비 내역 API")
@RestController
@RequestMapping("/api/accounts/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	private final AuthService authService;

	@Operation(
		summary = "전체 계좌 거래 내역 조회",
		description = "사용자의 전체 계좌 거래 내역을 조회합니다."
	)
	@GetMapping
	public ResponseEntity<ApiResult> getPayments(
		HttpServletRequest request,
		@RequestParam(required = false) Category category,
		@RequestParam(required = false) Long accountId,
		Pageable pageable
	) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		TotalPaymentResponseDto response = paymentService.getPayments(accessToken, accountId, category, pageable);
		return ResponseEntity.ok(new ApiResult(200, "OK", "통합 계좌 거래 내역 조회 성공", response));
	}

	@Operation(
		summary = "결제 내역 상세 조회",
		description = "사용자의 결제 내역 상세를 조회합니다."
	)
	@GetMapping("/{paymentId}")
	public ResponseEntity<ApiResult> getPaymentDetail(
		HttpServletRequest request,
		@PathVariable Long paymentId
	) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		PaymentResponseDto paymentResponseDto = paymentService.getPaymentById(paymentId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "결제 상세 조회 성공", paymentResponseDto));
	}

	@Operation(
		summary = "결제 내역 상세 등록",
		description = "사용자의 결제 내역 상세를 등록합니다."
	)
	@PostMapping("/details")
	public ResponseEntity<ApiResult> uploadPaymentDetail(
		HttpServletRequest request,
		@RequestBody PaymentListRequestDto requestDto) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		paymentService.uploadPaymentDetails(accessToken, requestDto);
		return ResponseEntity.ok(new ApiResult(200,"OK", "결제 내역 상세 업로드 성공", null));
	}

	@Operation(
		summary = "결제처 정보 업데이트",
		description = "사용자가 등록한 결제 내역의 결제처 정보를 업데이트합니다."
	)
	@PatchMapping
	public ResponseEntity<ApiResult> uploadPayments(
		HttpServletRequest request,
		@RequestBody PaymentUpdateRequest paymentRequest
	) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
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
