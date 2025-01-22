package com.example.PayAll_BE.dto.Payment;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentUpdateRequestDto {
	@JsonProperty("account_id")
	private Long accountId;

	@JsonProperty("payment_place")
	private String paymentPlace;

	@JsonProperty("payment_time")
	private LocalDateTime paymentTime;
}
