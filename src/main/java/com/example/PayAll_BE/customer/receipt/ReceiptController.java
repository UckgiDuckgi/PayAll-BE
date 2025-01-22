package com.example.PayAll_BE.customer.receipt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.customer.receipt.dto.ReceiptRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt")
public class ReceiptController {
	private final ReceiptService receiptService;

	@PostMapping
	public ResponseEntity<ApiResult> uploadReceipt(@RequestBody ReceiptRequestDto requestDto) {
		receiptService.uploadReceipt(requestDto);
		return ResponseEntity.ok(new ApiResult(200, "OK", "영수증 업로드 성공", null));
	}
}
