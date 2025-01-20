package com.example.PayAll_BE.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {
	CARD("카드"),
	SUBSCRIBE("구독");
	private String productType;
}
