package com.example.PayAll_BE.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardType {
	CREDIT("신용"),
	CHECK("체크");
	private final String cardType;
}
