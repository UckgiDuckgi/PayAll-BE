package com.example.PayAll_BE.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
	TOTAL("전체"),
	SHOPPING("쇼핑"),
	EDUCATION("교육"),
	LIVING("생활비"),
	TRANSPORT("교통비"),
	CULTURE("문화,여가"),
	RESTAURANT("음식점"),
	CAFE("카페"),
	HEALTH("병원비"),
	INCOME("수입"),
	OTHERS("기타"),
	SAVING("할인 누적 금액");
	private final String category;
}
