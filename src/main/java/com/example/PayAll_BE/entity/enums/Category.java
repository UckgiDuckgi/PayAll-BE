package com.example.PayAll_BE.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
	SHOPPING("쇼핑"),
	EDUCATION("교육"),
	LIVING("생활비"),
	TRANSPORT("교통비"),
	CULTURE("문화,여가"),
	RESTAURANT("음식점"),
	CAFE("카페"),
	HEALTH("병원비"),
	INCOME("수입"),
	OTHERS("기타");
	private final String category;
}
