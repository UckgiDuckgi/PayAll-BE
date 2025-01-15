package com.example.PayAll_BE.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.CardRecommendationResultDto;
import com.example.PayAll_BE.entity.CardBenefit;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.repository.CardBenefitsRepository;
import com.example.PayAll_BE.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecommendationService {

	private final PaymentRepository paymentRepository;
	private final CardBenefitsRepository cardBenefitsRepository;

	public List<CardRecommendationResultDto> getCardRecommendations(Long accountId) {
		// 1. 사용자 소비 데이터 조회
		List<Payment> payments = paymentRepository.findByAccountId(accountId);

		// 2. 카테고리별로 가장 소비가 많은 가맹점 찾기
		Map<Category, String> topStoresByCategory = payments.stream()
			.collect(Collectors.groupingBy(
				Payment::getCategory,
				Collectors.collectingAndThen(
					Collectors.maxBy(Comparator.comparing(Payment::getPrice)),
					optional -> optional.map(Payment::getPaymentPlace).orElse(null)
				)
			));

		List<CardRecommendationResultDto> recommendations = new ArrayList<>();

		// 3. 각 가맹점에 대해 혜택 폭이 가장 큰 카드 찾기 및 할인 금액 계산
		for (Map.Entry<Category, String> entry : topStoresByCategory.entrySet()) {
			Category category = entry.getKey();
			String paymentPlace = entry.getValue();

			// 카드 혜택 조회
			CardBenefit bestCard = cardBenefitsRepository.findTopByStoreNameOrderByBenefitValueDesc(paymentPlace);

			if (bestCard != null) {
				// 해당 가맹점의 총 소비 금액 계산
				long totalSpentAtPlace = payments.stream()
					.filter(payment -> payment.getPaymentPlace().equals(paymentPlace))
					.mapToLong(Payment::getPrice)
					.sum();

				long discountAmount = (long) (totalSpentAtPlace * (bestCard.getBenefitValue().doubleValue() / 100.0));

				// 추천 결과 저장
				recommendations.add(
					new CardRecommendationResultDto(bestCard.getCardName(), paymentPlace, discountAmount));
			}
		}

		return recommendations;
	}
}
