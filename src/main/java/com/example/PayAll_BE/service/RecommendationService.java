package com.example.PayAll_BE.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.ProductResponseDto;
import com.example.PayAll_BE.dto.RecommendationResponseDto;
import com.example.PayAll_BE.dto.StoreStatisticsDto;
import com.example.PayAll_BE.entity.Benefit;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.Recommendation;
import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.Store;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.repository.BenefitRepository;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.RecommendationRepository;
import com.example.PayAll_BE.repository.StatisticsRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecommendationService {

	private final PaymentRepository paymentRepository;
	private final BenefitRepository benefitRepository;
	private final RecommendationRepository recommendationRepository;
	private final StatisticsRepository statisticsRepository;

	private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

	public void generateBenefits(User user, LocalDateTime startDate,LocalDateTime endDate) {

		List<StoreStatisticsDto> storeStatisticsDtos = paymentRepository.getCategoryStoreStats(user.getId(),
			startDate, endDate);

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
		logger.info("Saved statisticsList:");

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
					.productType(benefit.getProduct().getProductType())
					.dateTime(startDate)
					.category(Category.valueOf(dto.getName()))
					.build();
			})
			.filter(Objects::nonNull)  // null인 항목은 필터링
			.collect(Collectors.toList());

		recommendationRepository.saveAll(recommendationList);
		logger.info("Saved recommendations:");
		logger.info("데이터 적재 성공");
	}

	public List<RecommendationResponseDto> getRecommendation(User user) {
		List<Recommendation> recommendations = recommendationRepository.findByUser(user);

		List<RecommendationResponseDto> recommendationDtos = recommendations.stream()
			.map(dto -> RecommendationResponseDto.builder()
				.storeName(dto.getStoreName())
				.discountAmount(dto.getDiscountAmount())
				.productName(dto.getProduct().getProductName())
				.productId(dto.getProduct().getId())
				.category(dto.getCategory())
				.productType(dto.getProductType())
				.build(
				))
			.collect(Collectors.toList());

		return recommendationDtos;
	}

	public List<ProductResponseDto> calculateDiscount(User user, Long productId) {
		LocalDateTime startDate = getStartOfLastMonth();
		LocalDateTime endDate = getEndOfLastMonth();

		List<Object[]> benefitsWithStores = benefitRepository.findBenefitsWithStoresByProductId(productId);

		List<ProductResponseDto> responseList = new ArrayList<>();

		Long totalDiscount = 0L;

		for (Object[] result : benefitsWithStores) {
			Benefit benefit = (Benefit)result[0];
			Store store = (Store)result[1];

			List<Payment> payments = paymentRepository.findByUserAndPaymentPlace(user.getId(), store.getStoreName(),
				startDate, endDate);

			if (payments.isEmpty()) {
				continue;
			}

			Long totalSpent = payments.stream()
				.mapToLong(Payment::getPrice)
				.sum();

			Long discountAmount = totalSpent * benefit.getBenefitValue() / 100;
			totalDiscount += discountAmount;

			ProductResponseDto responseDto = ProductResponseDto.builder()
				.productName(benefit.getProduct().getProductName())
				.benefitDescription(benefit.getProduct().getBenefitDescription())
				.category(store.getCategory())
				.storeName(store.getStoreName())
				.discountAmount(discountAmount)
				.visitCount(payments.size())
				.build();

			responseList.add(responseDto);
		}
		return responseList;
	}

	public void flagSetRecommendation(User user){
		// 현재 날짜에서 저번 달의 1일 00:00:00 계산
		LocalDateTime startOfLastMonth = LocalDate.now()
			.minusMonths(1)
			.withDayOfMonth(1)
			.atStartOfDay();

		LocalDateTime startOfThisMonth = LocalDate.now()
			.withDayOfMonth(1)
			.atStartOfDay();

		if (!recommendationRepository.existsRecommendationByDateTimeAndUser(startOfLastMonth,user)){
			generateBenefits(user,startOfLastMonth,startOfThisMonth);
		}
	}

	private LocalDate getStartOfMonthWithyearMonth(String yearMonth) {
		int year = Integer.parseInt(yearMonth.substring(0, 4));
		int month = Integer.parseInt(yearMonth.substring(4, 6));

		return LocalDate.of(year, month, 1);
	}

	private LocalDate getEndOfMonthWithyearMonth(String yearMonth) {
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

	private LocalDateTime getStartOfLastMonth() {
		LocalDate now = LocalDate.now();
		LocalDate firstDayOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
		return firstDayOfLastMonth.atStartOfDay();
	}

	private LocalDateTime getEndOfLastMonth() {
		LocalDate now = LocalDate.now();
		LocalDate lastDayOfLastMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());
		return lastDayOfLastMonth.atTime(23, 59, 59, 999999999); // 마지막 시간까지
	}

}
