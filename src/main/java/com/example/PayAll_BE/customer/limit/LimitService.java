package com.example.PayAll_BE.customer.limit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.customer.limit.dto.LimitRegisterRequestDto;
import com.example.PayAll_BE.customer.limit.dto.LimitResponseDto;
import com.example.PayAll_BE.customer.payment.Payment;
import com.example.PayAll_BE.customer.statistics.Statistics;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.customer.payment.PaymentRepository;
import com.example.PayAll_BE.customer.statistics.StatisticsRepository;
import com.example.PayAll_BE.customer.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LimitService {
	private final LimitRepository limitRepository;
	private final UserRepository userRepository;
	private final StatisticsRepository statisticsRepository;
	private final PaymentRepository paymentRepository;


	// 소비 목표 등록
	public void registerLimit(Long userId, LimitRegisterRequestDto limitRequestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

		LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
		LocalDateTime now = LocalDateTime.now();
		long averageSpent = calculateAverageSpent(userId, threeMonthsAgo, now);

		int currentYear = now.getYear();
		int currentMonth = now.getMonthValue();

		// 현재 달에 이미 등록된 소비 목표가 있는지 확인
		boolean exists = limitRepository.existsByUserIdAndMonth(userId, currentYear, currentMonth);
		if (exists) {
			throw new IllegalArgumentException("이미 이번 달에 소비 목표가 등록되었습니다.");
		}

		Limits limit = Limits.builder()
			.user(user)
			.limitPrice(limitRequestDto.getLimitPrice())
			.limitDate(LocalDateTime.now())
			.build();

		limitRepository.save(limit);
	}

	// 소비 목표 조회
	public LimitResponseDto getLimit(Long userId) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
		LocalDateTime today = now;

		// 현재 달의 소비 금액 계산
		long spentAmount = calculateSpentAmount(userId, startOfMonth, today);

		// 지난 3개월 평균 지출 계산
		LocalDateTime threeMonthsAgo = now.minusMonths(3);
		long averageSpent = calculateAverageSpent(userId, threeMonthsAgo, now);

		// 이전 달 소비 목표 조회
		int lastMonth = now.minusMonths(1).getMonthValue();
		int lastMonthYear = now.minusMonths(1).getYear();
		Limits lastMonthLimit = limitRepository.findFirstByUserIdAndLimitDateBetweenOrderByLimitDateDesc(
			userId,
			LocalDateTime.of(lastMonthYear, lastMonth, 1, 0, 0),
			LocalDateTime.of(lastMonthYear, lastMonth, 1, 0, 0).plusMonths(1).minusSeconds(1)
		).orElse(null);
		Long lastMonthLimitPrice = lastMonthLimit != null ? lastMonthLimit.getLimitPrice() : null;

		// 현재 소비 목표 조회
		Limits currentLimit = limitRepository.findTopByUser_IdOrderByLimitDateDesc(userId).orElse(null);

		if (currentLimit == null) {
			// 소비 목표를 등록한 적이 없는 경우
			return LimitResponseDto.builder()
				.userId(userId)
				.limitPrice(null)
				.limitPrice(null)
				.spentAmount(spentAmount)
				.savedAmount(null)
				.averageSpent(averageSpent)
				.lastMonthLimit(lastMonthLimitPrice)
				.startDate(startOfMonth.toLocalDate())
				.endDate(startOfMonth.plusMonths(1).minusDays(1).toLocalDate())
				.build();
		} else {
			// 소비 목표가 있는 경우
			long savedAmount = currentLimit.getLimitPrice() - spentAmount;
			return LimitResponseDto.builder()
				.limitId(currentLimit.getLimitId())
				.userId(currentLimit.getUser().getId())
				.limitPrice(currentLimit.getLimitPrice())
				.spentAmount(spentAmount)
				.savedAmount(savedAmount)
				.averageSpent(averageSpent)
				.lastMonthLimit(lastMonthLimitPrice)
				.startDate(currentLimit.getLimitDate().toLocalDate().withDayOfMonth(1))
				.endDate(currentLimit.getLimitDate().toLocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1))
				.build();
		}
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

	// 특정 기간 동안의 소비 금액 계산
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
