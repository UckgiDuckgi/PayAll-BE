package com.example.PayAll_BE.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.StoreStatisticsDto;
import com.example.PayAll_BE.entity.Benefit;
import com.example.PayAll_BE.entity.Product;
import com.example.PayAll_BE.entity.Recommendation;
import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.Store;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.repository.BenefitRepository;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.ProductRepository;
import com.example.PayAll_BE.repository.RecommendationRepository;
import com.example.PayAll_BE.repository.StatisticsRepository;
import com.example.PayAll_BE.repository.StoreRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecommendationService {

	private final PaymentRepository paymentRepository;
	private final BenefitRepository benefitRepository;
	private final ProductRepository productRepository;
	private final RecommendationRepository recommendationRepository;
	private final StatisticsRepository statisticsRepository;
	private final StoreRepository storeRepository;

	public void generateBenefits(User user,String yearMonth) {
		LocalDateTime startDate = getStartOfMonth(yearMonth).atStartOfDay();
		LocalDateTime endDate = getEndOfMonth(yearMonth).atStartOfDay();

		System.out.println("user = " + user.getId());
		List<StoreStatisticsDto> storeStatisticsDtos = paymentRepository.getCategoryStoreStats(user.getId(),
			startDate,endDate);

		System.out.println("storeStatisticsDtos = " + storeStatisticsDtos);
		storeStatisticsDtos.stream()
			.forEach(dto -> System.out.println("Type: " + Category.valueOf(dto.getName())));

		System.out.println("storeStatisticsDtos = " + storeStatisticsDtos);

		List<Statistics> statisticsList = storeStatisticsDtos.stream()
			.filter(dto -> dto.getType().equals("CATEGORY")) // 'CATEGORY'인 항목만 필터링
			.map(dto -> Statistics.builder()
				.user(user)
				.category(Category.valueOf(dto.getName()))  // Enum으로 변환
				.statisticsAmount(dto.getTotalSpent())
				.statisticsDate(startDate)  // startDate로 통일
				.build())
			.collect(Collectors.toList());
		// 'statisticsList'에 저장
		statisticsRepository.saveAll(statisticsList);

		List<Recommendation> recommendationList = storeStatisticsDtos.stream()
			.map(dto -> {
				Benefit benefit = benefitRepository.findByPaymentPlace(dto.getStore())
					.orElse(null);

				if (benefit == null) {
					return null;
				}

				Long discountAmount = dto.getTotalSpent() * benefit.getBenefitValue() / 100;

				return Recommendation.builder()
					.user(user)
					.storeName(dto.getStore())
					.visitCount(dto.getStorePurchaseCount())
					.product(benefit.getProduct())
					.discountAmount(discountAmount)
					.dateTime(startDate)
					.category(Category.valueOf(dto.getName()))
					.build();
			})
			.filter(Objects::nonNull)  // null인 항목은 필터링
			.collect(Collectors.toList());

		recommendationRepository.saveAll(recommendationList);
	}


	private LocalDate getStartOfMonth(String yearMonth) {
		int year = Integer.parseInt(yearMonth.substring(0, 4));
		int month = Integer.parseInt(yearMonth.substring(4, 6));

		return LocalDate.of(year, month, 1);
	}

	private LocalDate getEndOfMonth(String yearMonth) {
		int year = Integer.parseInt(yearMonth.substring(0, 4));
		int month = Integer.parseInt(yearMonth.substring(4, 6));
		if (month == 12) {
			year++;
			month = 1;
		} else {
			month++;
		}
		return LocalDate.of(year, month, 1);
	}

}
