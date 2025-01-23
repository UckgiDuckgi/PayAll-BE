package com.example.PayAll_BE.customer.payment.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentUpdateRequestDto {
	private Long accountId;
	private String paymentPlace;
	private LocalDateTime paymentTime;
}
