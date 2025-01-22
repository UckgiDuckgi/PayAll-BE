package com.example.PayAll_BE.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Purchase.PurchaseRequestDto;
import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.mydata.service.MydataService;
import com.example.PayAll_BE.repository.StatisticsRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {
	private final JwtService jwtService;
	private final MydataService mydataService;
	private final CartService cartService;
	private final PaymentService paymentService;
	private final StatisticsRepository statisticsRepository;
	private final UserRepository userRepository;

	@Value("${server1.base-url}")
	private String baseUrl;

	public void syncMydata(String token, PurchaseRequestDto purchaseRequestDto) {
		Long userId = jwtService.extractUserId(token);
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

		// 1. 마이데이터에 구매 내역 반영
		String accountNum = mydataService.syncPurchaseData(token, purchaseRequestDto);

		// 2. 마이데이터 연동
		mydataService.syncMydataInfo(token);

		// 3. payment_detail 반영
		paymentService.createPaymentDetails(userId, accountNum, purchaseRequestDto.getPurchaseList());

		// 4. cart clear
		purchaseRequestDto.getPurchaseList().forEach(product -> {
			cartService.deleteCart(product.getCartId(), authId);
		});

		// 5. statistics 테이블에 할인 금액 누적
		// 현재 날짜에서 1일 00:00:00 계산
		LocalDateTime startOfThisMonth = LocalDate.now()
			.withDayOfMonth(1)
			.atStartOfDay();

		Statistics existingStatistic = statisticsRepository.findByUserIdAndCategoryAndStatisticsDate(
			userId, Category.SAVING, startOfThisMonth).orElse(null);

		// 테이블에 없으면 새로운 statistics 생성
		if (existingStatistic == null) {
			existingStatistic = Statistics.builder()
				.user(user)
				.category(Category.SAVING)
				.statisticsAmount(purchaseRequestDto.getTotalDiscountPrice())
				.statisticsDate(startOfThisMonth)
				.build();
		} else {
			// 테이블에 있으면 기존 데이터 업데이트
			existingStatistic.setStatisticsAmount(
				existingStatistic.getStatisticsAmount() + purchaseRequestDto.getTotalDiscountPrice());
		}
		statisticsRepository.save(existingStatistic);

	}

}
