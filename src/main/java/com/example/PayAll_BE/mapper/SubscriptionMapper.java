package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.Subscription.SubscriptionResponseDto;
import com.example.PayAll_BE.entity.Subscription;

public class SubscriptionMapper {

	public static SubscriptionResponseDto toDto(Subscription subscription) {
		return SubscriptionResponseDto.builder()
			.subscriptionId(subscription.getSubscriptionId())
			.subscriptionName(subscription.getSubscriptionName())
			.monthlyFee(subscription.getMonthlyFee())
			.build();
	}
}
