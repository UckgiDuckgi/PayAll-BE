package com.example.PayAll_BE.customer.payment.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentUpdateRequest {
	private List<PaymentUpdateRequestDto> paymentList;
}
