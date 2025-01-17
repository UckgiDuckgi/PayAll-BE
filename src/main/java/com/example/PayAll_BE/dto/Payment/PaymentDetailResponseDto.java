package com.example.PayAll_BE.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailResponseDto {
	private String paymentPlace;
	private String category;
	private Long paymentPrice;
	private String paymentType;
	private LocalDateTime paymentTime;
	private String bankName;
	private String accountName;
	private Boolean shootNeed;
}
