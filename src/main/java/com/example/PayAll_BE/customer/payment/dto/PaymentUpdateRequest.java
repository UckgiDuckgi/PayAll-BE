package com.example.PayAll_BE.customer.payment.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentUpdateRequest {
	@JsonProperty("payment_list")
	private List<PaymentUpdateRequestDto> paymentList;
}
