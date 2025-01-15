package com.example.PayAll_BE.dto.Payment;

import lombok.Data;

@Data
public class PaymentRequestDto {
	private String top3Category;     // 최대 사용금액 카테고리
	private String paymentPlace;     // 사용처
	private Long totalPaymentPrice;  // 사용 금액 합계
}
