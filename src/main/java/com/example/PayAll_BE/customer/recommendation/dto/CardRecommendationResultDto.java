package com.example.PayAll_BE.customer.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardRecommendationResultDto {
		private String cardName;
		private String paymentPlace;
		private Long discountAmount;
}
