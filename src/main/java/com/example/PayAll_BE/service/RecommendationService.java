package com.example.PayAll_BE.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.CardRecommendationResultDto;
import com.example.PayAll_BE.dto.StoreStatisticsDto;
import com.example.PayAll_BE.entity.Benefit;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.Product;
import com.example.PayAll_BE.entity.Recommendation;
import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.repository.BenefitRepository;
import com.example.PayAll_BE.repository.CardBenefitRepository;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.ProductRepository;
import com.example.PayAll_BE.repository.RecommendationRepository;
import com.example.PayAll_BE.repository.StatisticsRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecommendationService {

	private final PaymentRepository paymentRepository;
	private final CardBenefitRepository cardBenefitsRepository;
	private final UserRepository userRepository;
	private final BenefitRepository benefitRepository;
	private final ProductRepository productRepository;
	private final RecommendationRepository recommendationRepository;
	private StatisticsRepository statisticsRepository;

	public void generateBenefits(User user) {
		List<StoreStatisticsDto> storeStatisticsDtos = paymentRepository.getCategoryStoreStats(user.getId());

		List<Statistics> statisticsList = storeStatisticsDtos.stream()
			.map(dto -> Statistics.builder()
				.user(user)
				.category(dto.getCategory())
				.statisticsAmount(dto.getTotalSpent())
				.build())
			.collect(Collectors.toList());

		statisticsRepository.saveAll(statisticsList);

		//알맞은 카드는 사용자의 paymentplace를 아니까 paymentplace가 있는 benefit을 찾아서 product를 가져옴
		List<Recommendation> recommendationList = storeStatisticsDtos.stream()
			.map(dto -> {
				Benefit benefit = benefitRepository.findByPaymentPlace(dto.getPaymentPlace())
					.orElseThrow(() -> new IllegalArgumentException("일치하는 store가 없습니다."));

				Product product = productRepository.findById(benefit.getId())
					.orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));

				BigDecimal storeTotalSpent = new BigDecimal(dto.getStoreTotalSpent());
				long discountAmount = storeTotalSpent
					.multiply(benefit.getBenefitValue())
					.longValue(); // 할인 금액 계산

				return Recommendation.builder()
					.user(user)
					.storeName(dto.getPaymentPlace())
					.visitCount(dto.getStorePurchaseCount())
					.product(product)
					.discountAmount(discountAmount)
					.category(dto.getCategory())
					.build();
			})
			.toList();

		recommendationRepository.saveAll(recommendationList);
	}

}
