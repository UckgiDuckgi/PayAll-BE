package com.example.PayAll_BE.customer.recommendation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.customer.product.dto.ProductResponseDto;
import com.example.PayAll_BE.customer.recommendation.dto.RecommendProductDto;
import com.example.PayAll_BE.customer.recommendation.dto.RecommendationResponseDto;
import com.example.PayAll_BE.customer.statistics.dto.StoreStatisticsDto;
import com.example.PayAll_BE.customer.benefit.Benefit;
import com.example.PayAll_BE.customer.payment.Payment;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetail;
import com.example.PayAll_BE.customer.statistics.Statistics;
import com.example.PayAll_BE.customer.store.Store;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.benefit.BenefitRepository;
import com.example.PayAll_BE.customer.payment.PaymentRepository;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.product.ProductApiClient;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetailRepository;
import com.example.PayAll_BE.customer.statistics.StatisticsRepository;
import com.example.PayAll_BE.customer.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecommendationService {

	private final PaymentRepository paymentRepository;
	private final BenefitRepository benefitRepository;
	private final RecommendationRepository recommendationRepository;
	private final StatisticsRepository statisticsRepository;
	private final PaymentDetailRepository paymentDetailRepository;
	private final ProductApiClient productApiClient;
	private final UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

	public void generateBenefits(User user, LocalDateTime startDate, LocalDateTime endDate) {

		List<StoreStatisticsDto> storeStatisticsDtos = paymentRepository.getCategoryStoreStats(user.getId(),
			startDate, endDate);

		System.out.println("storeStatisticsDtos = " + storeStatisticsDtos);
		List<Statistics> statisticsList = storeStatisticsDtos.stream()
			.filter(dto -> dto.getType().equals("CATEGORY") && dto.getTotalSpent() != null) // 'CATEGORY'인 항목만 필터링
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

	public ProductResponseDto calculateDiscount(User user, Long productId) {
		LocalDateTime startDate = getStartOfLastMonth();
		LocalDateTime endDate = getEndOfLastMonth();

		List<Object[]> benefitsWithStores = benefitRepository.findBenefitsWithStoresByProductId(productId);
		List<ProductResponseDto.StoreDetailDto> storeDetailList = new ArrayList<>();

		Long totalDiscount = 0L;
		Benefit benefit = null;

		for (Object[] result : benefitsWithStores) {
			benefit = (Benefit)result[0];
			Store store = (Store)result[1];

			// 유저와 매장 이름에 기반하여 결제 내역 조회
			List<Payment> payments = paymentRepository.findByUserAndPaymentPlace(user.getId(), store.getStoreName(),
				startDate, endDate);

			if (payments.isEmpty()) {
				continue; // 결제 내역이 없으면 스킵
			}

			// 총 지출 계산
			Long totalSpent = payments.stream()
				.mapToLong(Payment::getPrice)
				.sum();

			// 할인 금액 계산
			Long discountAmount = totalSpent * benefit.getBenefitValue() / 100;

			// StoreDetailDto 생성 및 추가
			ProductResponseDto.StoreDetailDto storeDetail = ProductResponseDto.StoreDetailDto.builder()
				.category(store.getCategory())
				.storeName(store.getStoreName())
				.discountAmount(discountAmount)
				.visitCount(payments.size())
				.build();

			storeDetailList.add(storeDetail);
		}

		// ProductResponseDto 생성
		ProductResponseDto responseDto = ProductResponseDto.builder()
			.productName(benefit.getProduct().getProductName())
			.benefitDescription(benefit.getProduct().getBenefitDescription())
			.storeDetails(storeDetailList)
			.build();

		return responseDto;
	}

	public void flagSetRecommendation(User user) {
		// 현재 날짜에서 저번 달의 1일 00:00:00 계산
		LocalDateTime startOfLastMonth = LocalDate.now()
			.minusMonths(1)
			.withDayOfMonth(1)
			.atStartOfDay();

		LocalDateTime startOfThisMonth = LocalDate.now()
			.withDayOfMonth(1)
			.atStartOfDay();

		if (!recommendationRepository.existsRecommendationByDateTimeAndUser(startOfLastMonth, user)) {
			generateBenefits(user, startOfLastMonth, startOfThisMonth);
		}
	}

	public List<RecommendProductDto> getRecommendProducts(String authId) {
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));
		// 최근 결제 내역 조회
		int size = 10;
		List<PaymentDetail> recentPayments = paymentDetailRepository.findRecentPaymentsByUserId(user.getId(),
			PageRequest.of(0, size));

		logger.info("Found {} recent payments", recentPayments.size());

		// 최근 지출 내역 없음
		if (recentPayments.isEmpty()) {
			logger.warn("No payment details found for userId: {}", user.getId());
			return Collections.emptyList();
		}

		return recentPayments.stream()
			.map(this::getProductFromRedis)
			.filter(Objects::nonNull) // null인 경우 제거
			.distinct()   // 중복 상품 제거
			.toList();
	}

	private RecommendProductDto getProductFromRedis(PaymentDetail paymentDetail) {
		Long productId = paymentDetail.getProductId();
		ProductDto productDto = productApiClient.fetchProduct(String.valueOf(productId));

		// 할인율 계산
		Long prevPrice = paymentDetail.getProductPrice();
		Long currPrice = productDto.getPrice();
		if (prevPrice <= currPrice) {
			return null;
		}
		Double discountRate = ((double)(prevPrice - currPrice) / prevPrice) * 100;

		return RecommendProductDto.builder()
			.productId(productId)
			.productName(productDto.getProductName())
			.productImage(productDto.getProductImage())
			.price(productDto.getPrice())
			.storeName(productDto.getShopName())
			.link(productDto.getShopUrl())
			.discountRate(discountRate)
			.build();

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
