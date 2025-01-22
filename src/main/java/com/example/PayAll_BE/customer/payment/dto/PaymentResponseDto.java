package com.example.PayAll_BE.customer.payment.dto;

import 	lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentDetailDto;

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
	private List<PaymentDetailDto> paymentDetailList;
}
