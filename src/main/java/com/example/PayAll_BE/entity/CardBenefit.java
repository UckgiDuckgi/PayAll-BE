package com.example.PayAll_BE.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CardBenefit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // Primary Key

	private String cardName; // 카드 이름

	private String storeName; // 혜택 적용 가맹점 이름 (예: 스타벅스)

	@Enumerated(EnumType.STRING)
	private Category category; // 혜택 적용 카테고리 (예: FOOD, ENTERTAINMENT)

	private BigDecimal benefitValue; // 혜택 비율 (예: 10.00 -> 10%)

	private String benefitType; // 혜택 종류 (예: 할인, 캐시백)

	private String additionalConditions; // 추가 조건 (예: 월 최대 30만 원 한도)

	// Enums for category
	public enum Category {
		FOOD, TRANSPORT, ENTERTAINMENT, SHOPPING, OTHER
	}
}
