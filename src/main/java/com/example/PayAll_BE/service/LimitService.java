package com.example.PayAll_BE.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Limit.LimitRegisterRequestDto;
import com.example.PayAll_BE.dto.Limit.LimitRegisterResponseDto;
import com.example.PayAll_BE.dto.Limit.LimitResponseDto;
import com.example.PayAll_BE.entity.Limits;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.repository.LimitRepository;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.StatisticsRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LimitService {
	private final LimitRepository limitRepository;
	private final UserRepository userRepository;
	private final StatisticsRepository statisticsRepository;
	private final PaymentRepository paymentRepository;


	// 소비 목표 등록
	public LimitRegisterResponseDto registerLimit(Long userId, LimitRegisterRequestDto limitRequestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

		LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
		LocalDateTime now = LocalDateTime.now();
		long averageSpent = calculateAverageSpent(userId, threeMonthsAgo, now);

		Limits limit = Limits.builder()
			.user(user)
			.limitPrice(limitRequestDto.getLimitPrice())
			.limitDate(LocalDateTime.now())
			.build();

		Limits savedLimit = limitRepository.save(limit);

		return LimitRegisterResponseDto.builder()
			.limitId(savedLimit.getLimitId())
			.userId(userId)
			.limitPrice(savedLimit.getLimitPrice())
			.limitDate(savedLimit.getLimitDate())
			.build();
	}

	// 소비 목표 조회
	public LimitResponseDto getLimit(Long userId) {
		// 가장 최근 소비 목표 조회
		Limits limit = limitRepository.findTopByUser_IdOrderByLimitDateDesc(userId)
			.orElseThrow(() -> new IllegalArgumentException("소비 목표가 존재하지 않습니다."));

		// 지난 3개월 평균 지출 계산
		LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
		LocalDateTime now = LocalDateTime.now();
		long averageSpent = calculateAverageSpent(userId, threeMonthsAgo, now);

		// 기간 계산
		LocalDate startDate = calculateStartDate(limit.getLimitDate());
		LocalDate endDate = calculateEndDate(startDate);

		// 지난달 계산
		int lastMonth = now.minusMonths(1).getMonthValue();
		int lastMonthYear = now.minusMonths(1).getYear();

		// 지난달 소비 목표 조회
		Limits lastMonthLimit = limitRepository.findFirstByUserIdAndLimitDateBetweenOrderByLimitDateDesc(
			userId,
			LocalDateTime.of(lastMonthYear, lastMonth, 1, 0, 0),
			LocalDateTime.of(lastMonthYear, lastMonth, 1, 0, 0).plusMonths(1).minusSeconds(1)
		).orElse(null);

		Long lastMonthLimitPrice = lastMonthLimit != null ? lastMonthLimit.getLimitPrice() : null;

		LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
		LocalDateTime today = now; // 오늘

		// 현재 달의 소비 금액 계산
		long spentAmount = calculateSpentAmount(userId, startOfMonth, today);

		// 절약 금액 계산
		long savedAmount = limit.getLimitPrice() - spentAmount;

		return LimitResponseDto.builder()
			.limitId(limit.getLimitId())
			.userId(limit.getUser().getId())
			.limitPrice(limit.getLimitPrice())
			.spentAmount(spentAmount) // 이번달 현재 날짜까지 소비금액
			.savedAmount(savedAmount) // 목표금액 - 소비금액
			.averageSpent(averageSpent) // 평균 지출 추가
			.lastMonthLimit(lastMonthLimitPrice) // 지난달 소비 목표 금액
			.startDate(startDate) // 기간 시작 날짜 추가
			.endDate(endDate) // 기간 종료 날짜 추가
			.build();
	}

	// 지난 3개월간 평균 지출 계산 메소드
	private long calculateAverageSpent(Long userId, LocalDateTime fromDate, LocalDateTime toDate) {
		List<Statistics> lastThreeMonthsStats = statisticsRepository.findByUserIdAndStatisticsDateBetween(
			userId, fromDate, toDate
		);

		long totalSpent = lastThreeMonthsStats.stream()
			.mapToLong(Statistics::getStatisticsAmount)
			.sum();

		// 데이터가 없는 경우 : 0 반환
		return lastThreeMonthsStats.isEmpty() ? 0 : totalSpent / 3;
	}

	private long calculateSpentAmount(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
		List<Payment> payments = paymentRepository.findByAccount_User_IdAndPaymentTimeBetween(userId, startDate, endDate);
		return payments.stream()
			.mapToLong(Payment::getPrice)
			.sum();
	}

	private LocalDate calculateStartDate(LocalDateTime limitDate) {
		return limitDate.toLocalDate().withDayOfMonth(1); // 해당 달의 첫째 날
	}

	private LocalDate calculateEndDate(LocalDate startDate) {
		return startDate.plusMonths(1).minusDays(1); // 해당 달의 마지막 날
	}
}
