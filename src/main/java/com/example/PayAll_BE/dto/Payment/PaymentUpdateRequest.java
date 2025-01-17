package com.example.PayAll_BE.dto.Payment;

import java.util.List;

import lombok.Data;

@Data
public class PaymentUpdateRequest {
	private List<PaymentUpdateRequestDto> paymentList;
}
