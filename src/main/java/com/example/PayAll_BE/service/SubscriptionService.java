package com.example.PayAll_BE.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Payment.PaymentRequestDto;
import com.example.PayAll_BE.dto.Subscription.SubscriptionRecommendResponseDto;
import com.example.PayAll_BE.dto.Subscription.SubscriptionRequestDto;
import com.example.PayAll_BE.dto.Subscription.SubscriptionResponseDto;
import com.example.PayAll_BE.entity.Subscription;
import com.example.PayAll_BE.mapper.SubscriptionMapper;
import com.example.PayAll_BE.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
	private final SubscriptionRepository subscriptionRepository;

	public List<SubscriptionResponseDto> getAllSubscriptions() {
		List<Subscription> subscriptions = subscriptionRepository.findAll();
		return subscriptions.stream()
			.map(SubscriptionMapper::toDto)
			.collect(Collectors.toList());
	}

	public List<SubscriptionRecommendResponseDto> getRecommendations(String accessToken, List<PaymentRequestDto> payments) {
		List<SubscriptionRecommendResponseDto> recommendations = new ArrayList<>();

		payments.forEach(payment -> {
			String category = payment.getTop3Category();
			Long totalPaymentPrice = payment.getTotalPaymentPrice();
			Subscription bestSubscription = findBestSubscriptionByCategory(category);

			if (bestSubscription != null) {
				double benefitRate = getBenefitRateByCategory(bestSubscription, category);
				long expectedBenefit = Math.round(totalPaymentPrice * benefitRate);

				recommendations.add(SubscriptionRecommendResponseDto.builder()
					.subscriptionName(bestSubscription.getSubscriptionName())
					.mostPaymentCategory(category)
					.mostPaymentPlace(payment.getPaymentPlace())
					.lastExpectBenefit(expectedBenefit)
					.build());
			}
		});

		return recommendations;
	}


	private Subscription findBestSubscriptionByCategory(String category) {
		switch (category.toUpperCase()) {
			case "SHOPPING":
				return subscriptionRepository.findTopShoppingBenefit().stream().findFirst().orElse(null);
			case "EDUCATION":
				return subscriptionRepository.findTopEducationBenefit().stream().findFirst().orElse(null);
			case "LIVING":
				return subscriptionRepository.findTopLivingBenefit().stream().findFirst().orElse(null);
			case "TRANSPORT":
				return subscriptionRepository.findTopTransportBenefit().stream().findFirst().orElse(null);
			case "CULTURE":
				return subscriptionRepository.findTopCultureBenefit().stream().findFirst().orElse(null);
			case "RESTAURANT":
				return subscriptionRepository.findTopRestaurantBenefit().stream().findFirst().orElse(null);
			case "CAFE":
				return subscriptionRepository.findTopCafeBenefit().stream().findFirst().orElse(null);
			case "HEALTH":
				return subscriptionRepository.findTopHealthBenefit().stream().findFirst().orElse(null);
			default:
				return null;
		}
	}

	private double getBenefitRateByCategory(Subscription subscription, String category) {
		switch (category.toUpperCase()) {
			case "SHOPPING":
				return subscription.getShoppingBenefitRate();
			case "EDUCATION":
				return subscription.getEducationBenefitRate();
			case "LIVING":
				return subscription.getLivingBenefitRate();
			case "TRANSPORT":
				return subscription.getTransportBenefitRate();
			case "CULTURE":
				return subscription.getCultureBenefitRate();
			case "RESTAURANT":
				return subscription.getRestaurantBenefitRate();
			case "CAFE":
				return subscription.getCafeBenefitRate();
			case "HEALTH":
				return subscription.getHealthBenefitRate();
			default:
				return 0.0;
		}
	}
}
