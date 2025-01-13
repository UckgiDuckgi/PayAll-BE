package com.example.PayAll_BE.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatisticsCategory {
	SHOPPING("쇼핑"),
	EDUCATION("교육"),
	HOME("가정, 생활"),
	TRANSPORTATION("교통"),
	CULTURE("문화시설"),
	DINING("외식"),
	CAFE("카페"),
	MEDICAL("의료");

	private final String category;
}
