package com.example.PayAll_BE.dto.Payment;

import 	lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PaymentResponseDto {
	private String paymentPlace;
	private String category;
	private String paymentType;
	private LocalDateTime paymentTime;
	private String bankName;
	private String accountName;
	private Long paymentPrice;
	private List<com.example.PayAll_BE.dto.Payment.PaymentDetailDto> paymentDetailList;
}
