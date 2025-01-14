package com.example.PayAll_BE.dto.Subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionResponseDto {
	private Long subscriptionId;
	private String subscriptionName;
	private Long monthlyFee;
}
