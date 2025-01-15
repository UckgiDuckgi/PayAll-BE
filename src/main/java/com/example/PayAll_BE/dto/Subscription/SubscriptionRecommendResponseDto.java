package com.example.PayAll_BE.dto.Subscription;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionRecommendResponseDto {
	private String subscriptionName;
	private String mostPaymentCategory;
	private String mostPaymentPlace;
	private Long lastExpectBenefit;
}
