package com.example.PayAll_BE.customer.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayPaymentResponseDto {
	private LocalDateTime paymentDate;
	private Long dayPaymentPrice;
	private List<PaymentDetailResponseDto> paymentDetail;
}
