package com.example.PayAll_BE.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRecommendationResultDto {
		private String cardName;
		private String paymentPlace;
		private Long discountAmount;
}
