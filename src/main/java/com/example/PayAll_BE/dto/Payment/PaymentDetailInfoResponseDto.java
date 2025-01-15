package com.example.PayAll_BE.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDetailInfoResponseDto {
	private Long paymentDetailId;
	private Long paymentId;
	private String productName;
	private Long price;
	private int amount;
}
