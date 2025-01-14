package com.example.PayAll_BE.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.dto.RecommendProductDto;
import com.example.PayAll_BE.entity.PaymentDetail;
import com.example.PayAll_BE.product.ProductApiClient;
import com.example.PayAll_BE.repository.PaymentDetailRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {
	private final PaymentDetailRepository paymentDetailRepository;
	private final ProductApiClient productApiClient;

	public List<RecommendProductDto> getRecommendProducts(Long userId) {
		System.out.println("userId = " + userId);
		// 최근 결제 내역 조회
		int size = 10;
		List<PaymentDetail> recentPayments = paymentDetailRepository.findRecentPaymentsByUserId(userId,
			PageRequest.of(0, size));

		log.info("Found {} recent payments", recentPayments.size());

		// 최근 지출 내역 없음
		if (recentPayments.isEmpty()) {
			log.warn("No payment details found for userId: {}", userId);
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

}
