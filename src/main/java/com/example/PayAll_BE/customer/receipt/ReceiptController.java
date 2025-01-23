package com.example.PayAll_BE.customer.receipt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.customer.receipt.dto.ReceiptRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Receipt", description = "영수증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt")
public class ReceiptController {
	private final ReceiptService receiptService;

	@Operation(
		summary = "영수증 등록",
		description = "사용자가 오프라인에서 결제한 영수증을 등록합니다."
	)
	@PostMapping
	public ResponseEntity<ApiResult> uploadReceipt(@RequestBody ReceiptRequestDto requestDto) {
		receiptService.uploadReceipt(requestDto);
		return ResponseEntity.ok(new ApiResult(200, "OK", "영수증 업로드 성공", null));
	}
}
