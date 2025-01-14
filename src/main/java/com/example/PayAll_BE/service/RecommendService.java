package com.example.PayAll_BE.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.dto.RecommendProductDto;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.PaymentDetail;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendService {
	private final PaymentRepository paymentRepository;
	private final RestTemplate restTemplate;

	private final String productApiUrl = "http://localhost:8081/redis/product/";

	public List<RecommendProductDto> getRecommendProducts(Long userId) {

		// 최근 결제 내역 조회
		int size = 10;
		List<Payment> recentPayments = paymentRepository.findRecentPaymentsByUserId(userId,
			PageRequest.of(0, size));

		// 지출 내역에서 각 상품의 상세 정보 가져오기
		List<PaymentDetail> paymentDetails = recentPayments.stream()
			.flatMap(payment -> payment.getPaymentDetails().stream())
			.toList();

		return paymentDetails.stream()
			.map(this::getProductFromRedis)
			.filter(Objects::nonNull) // null인 경우 제거
			.distinct()   // 중복 상품 제거
			.toList();
	}

	private RecommendProductDto getProductFromRedis(PaymentDetail paymentDetail) {
		// todo. 수정 필요
		Long productId = 11L;
		// 상품 정보 조회
		ResponseEntity<ProductDto> response = restTemplate.getForEntity(
			productApiUrl + productId,
			ProductDto.class);
		if (response.getBody() == null) {
			throw new NotFoundException("상품 id가 없습니다.");
		}
		ProductDto productDto = response.getBody();

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
			.discountRate(discountRate)
			.build();

	}

}
