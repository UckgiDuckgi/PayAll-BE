package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.Subscription.SubscriptionResponseDto;
import com.example.PayAll_BE.entity.Subscription;

public class SubscriptionMapper {

	public static SubscriptionResponseDto toDto(Subscription subscription) {
		return SubscriptionResponseDto.builder()
			.subscriptionName(subscription.getSubscriptionName())
			.monthlyFee(subscription.getMonthlyFee())
			/*
			카테고리 별 혜택률 추가 가능
			 */
			.build();
	}
}
