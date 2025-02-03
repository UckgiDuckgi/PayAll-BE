package com.example.PayAll_BE.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {
	ONLINE("온라인"),
	OFFLINE("오프라인"),
	PAYALL("PayAll");
	private final String paymentType;
}
