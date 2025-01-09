package com.example.PayAll_BE.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.repository.CardBenefitsRepository;
import com.example.PayAll_BE.repository.TransactionRepository;

@Service
public class RecommendationService {

	private final TransactionRepository transactionRepository;
	private final CardBenefitsRepository cardBenefitsRepository;

	public RecommendationService(TransactionRepository transactionRepository,
		CardBenefitsRepository cardBenefitsRepository) {
		this.transactionRepository = transactionRepository;
		this.cardBenefitsRepository = cardBenefitsRepository;
	}

	/**
	 * 사용자별 카드 추천 로직
	 * @param userId 사용자 ID
	 * @return 카드 추천 결과 리스트
	 */
	public List<CardRecommendationResult> getCardRecommendations(Long userId) {
		// 1. 사용자 소비 데이터 조회
		List<Transaction> transactions = transactionRepository.findByUserId(userId);

		// 2. 카테고리별로 가장 소비가 많은 가맹점 찾기
		Map<String, String> topMerchantsByCategory = transactions.stream()
			.collect(Collectors.groupingBy(
				Transaction::getCategory,
				Collectors.collectingAndThen(
					Collectors.maxBy(Comparator.comparing(Transaction::getAmount)),
					optional -> optional.map(Transaction::getMerchantName).orElse(null)
				)
			));

		List<CardRecommendationResult> recommendations = new ArrayList<>();

		// 3. 각 가맹점에 대해 혜택 폭이 가장 큰 카드 찾기 및 할인 금액 계산
		for (Map.Entry<String, String> entry : topMerchantsByCategory.entrySet()) {
			String category = entry.getKey();
			String merchantName = entry.getValue();

			// 카드 혜택 조회
			CardBenefits bestCard = cardBenefitsRepository.findTopByMerchantNameOrderByBenefitValueDesc(merchantName);

			if (bestCard != null) {
				// 저번 소비 데이터에서 해당 가맹점의 소비 금액
				BigDecimal merchantTotalAmount = transactions.stream()
					.filter(t -> t.getMerchantName().equals(merchantName))
					.map(Transaction::getAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

				// 할인 금액 계산
				BigDecimal discountAmount = merchantTotalAmount.multiply(bestCard.getBenefitValue().divide(new BigDecimal(100)));

				// 추천 결과 저장
				recommendations.add(new CardRecommendationResult(bestCard.getCardName(), merchantName, discountAmount));
			}
		}

		return recommendations;
	}
}
