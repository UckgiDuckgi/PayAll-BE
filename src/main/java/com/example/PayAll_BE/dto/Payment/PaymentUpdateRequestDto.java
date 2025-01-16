package com.example.PayAll_BE.dto.Payment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentUpdateRequestDto {
	private Long accountId;
	private String paymentPlace;  // 바꾸려는 실제 결제처
	private LocalDateTime paymentTime;
}
