package com.example.PayAll_BE.customer.statistics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.customer.statistics.dto.StatisticsDetailResponseDto;
import com.example.PayAll_BE.customer.statistics.dto.StatisticsResponseDto;
import com.example.PayAll_BE.customer.payment.Payment;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.payment.PaymentRepository;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {
	private final StatisticsRepository statisticsRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	public void setStatistics(User user){

		LocalDateTime firstDayOfThisMonth = LocalDateTime.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1, 0, 0, 0, 0);
		LocalDateTime firstDayOfNextMonth = firstDayOfThisMonth.plusMonths(1);

		List<Statistics> statisticsList = statisticsRepository.findByUserAndThisMonth(user, firstDayOfThisMonth, firstDayOfNextMonth);

		LocalDateTime lastUpdateTime;
		if (!statisticsList.isEmpty()) {
			lastUpdateTime = statisticsList.get(0).getStatisticsDate();
		} else {
			lastUpdateTime = firstDayOfThisMonth;
		}

		LocalDateTime currentTime = LocalDateTime.now();
		List<Payment> recentPayments = paymentRepository.findByUserAndPaymentTimeAfter(user, lastUpdateTime, currentTime);

		Map<Category, Long> categorySpent = new HashMap<>();
		for (Payment payment : recentPayments) {
			Category category = payment.getCategory();
			categorySpent.put(category, categorySpent.getOrDefault(category, 0L) + payment.getPrice());
		}

		for (Map.Entry<Category, Long> entry : categorySpent.entrySet()) {
			Category category = entry.getKey();
			Long totalSpent = entry.getValue();

			Statistics categoryStatistics = statisticsRepository.findByUserAndCategoryAndStatisticsDate(user, category, lastUpdateTime);

			if (categoryStatistics != null) {
				categoryStatistics.setStatisticsAmount(totalSpent);
				categoryStatistics.setStatisticsDate(currentTime);
			} else {
				categoryStatistics = Statistics.builder()
					.user(user)
					.category(category)
					.statisticsAmount(totalSpent)
					.statisticsDate(currentTime)
					.build();
			}

			statisticsRepository.save(categoryStatistics);
		}

	}
	public StatisticsResponseDto getStatistics(String token, String date) {

		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

		LocalDate startDate = LocalDate.parse(date + "-01");
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = startDate.plusMonths(1).atStartOfDay().minusSeconds(1);

		List<Statistics> statistics = statisticsRepository.findByUserIdAndStatisticsDateBetween(user.getId(), startDateTime, endDateTime);

		// 총 지출 계산
		long totalSpent = statistics.stream().mapToLong(Statistics::getStatisticsAmount).sum();

		// 카테고리별 지출 계산
		List<StatisticsResponseDto.CategoryExpense> categoryExpenses = statistics.stream()
			.collect(Collectors.groupingBy(
				Statistics::getCategory,
				Collectors.summingLong(Statistics::getStatisticsAmount)
			))
			.entrySet().stream()
			.map(entry -> new StatisticsResponseDto.CategoryExpense(
				entry.getKey().ordinal(), // 카테고리 ID
				entry.getKey().name(), // 카테고리 이름
				entry.getValue() // 지출 금액
			))
			.collect(Collectors.toList());

		int daysInMonth = startDate.lengthOfMonth(); // 월별 일수
		long dateAverage = totalSpent / daysInMonth; // 하루 평균 지출

		// 전월 대비 차이 계산
		LocalDate previousMonthStart = startDate.minusMonths(1);
		LocalDateTime previousStartDateTime = previousMonthStart.atStartOfDay();
		LocalDateTime previousEndDateTime = previousMonthStart.plusMonths(1).atStartOfDay().minusSeconds(1);

		List<Statistics> previousStatistics = statisticsRepository.findByUserIdAndStatisticsDateBetween(
			user.getId(), previousStartDateTime, previousEndDateTime);
		long previousTotalSpent = previousStatistics.stream().mapToLong(Statistics::getStatisticsAmount).sum();
		long difference = totalSpent - previousTotalSpent;

		// 고정 지출 데이터 조회
		LocalDateTime lastMonthStart = startDate.minusMonths(1).atStartOfDay();
		LocalDateTime lastMonthEnd = lastMonthStart.plusMonths(1).minusSeconds(1);
		LocalDateTime twoMonthsAgoStart = startDate.minusMonths(2).atStartOfDay();
		LocalDateTime twoMonthsAgoEnd = twoMonthsAgoStart.plusMonths(1).minusSeconds(1);

		List<Payment> fixedPayments = paymentRepository.findFixedExpenses(
			user.getId(),
			startDateTime,
			endDateTime,
			lastMonthStart,
			lastMonthEnd,
			twoMonthsAgoStart,
			twoMonthsAgoEnd
		);

		List<StatisticsResponseDto.FixedExpense> fixedExpenses = fixedPayments.stream()
			.map(payment -> new StatisticsResponseDto.FixedExpense(
				payment.getId().intValue(),
				payment.getPaymentPlace(),
				payment.getPrice(),
				payment.getPaymentTime().toLocalDate().toString()
			))
			.toList();

		return StatisticsResponseDto.builder()
			.name(user.getName())
			.date(date)
			.totalSpent(totalSpent)
			.dateAverage(dateAverage) // 하루 평균 지출
			.difference(difference) // 전월 대비 차이
			.categoryExpenses(categoryExpenses)
			.fixedExpenses(fixedExpenses) // 고정 지출
			.build();
	}

	public StatisticsDetailResponseDto getCategoryDetails(Long userId, Category category, String date) {

		LocalDate startDate = LocalDate.parse(date + "-01");
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = startDate.plusMonths(1).atStartOfDay().minusSeconds(1);

		// 해당 카테고리의 결제 내역 조회
		List<Payment> payments = paymentRepository.findByAccount_User_IdAndCategoryAndPaymentTimeBetween(
			userId, category, startDateTime, endDateTime
		);

		// 총 소비 금액 계산
		long totalSpent = payments.stream().mapToLong(Payment::getPrice).sum();

		// 소비 내역이 없을 경우
		if (payments.isEmpty()) {
			return StatisticsDetailResponseDto.builder()
				.categoryId(category.ordinal())
				.categoryName(category.name())
				.totalSpent(0)
				.transactions(List.of())
				.build();
		}

		// 날짜별 소비 내역 그룹화
		Map<LocalDate, List<Payment>> paymentsByDate = payments.stream()
			.collect(Collectors.groupingBy(payment -> payment.getPaymentTime().toLocalDate()));

		List<StatisticsDetailResponseDto.TransactionDetail> transactionDetails = paymentsByDate.entrySet().stream()
			.map(entry -> {
				LocalDate transactionDate = entry.getKey();
				List<Payment> dailyPayments = entry.getValue();

				// 날짜별 소비 금액
				long dateSpent = dailyPayments.stream().mapToLong(Payment::getPrice).sum();

				// 세부 내역 리스트 생성
				List<StatisticsDetailResponseDto.TransactionDetail.HistoryDetail> historyDetails = dailyPayments.stream()
					.map(payment -> new StatisticsDetailResponseDto.TransactionDetail.HistoryDetail(
						payment.getPaymentPlace(),
						category.name(), // 태그? 배지? -> 카테고리 이름 사용
						payment.getPrice(),
						payment.getPaymentType().name(),
						payment.getPaymentTime().toLocalTime().toString()
					))
					.collect(Collectors.toList());

				return new StatisticsDetailResponseDto.TransactionDetail(
					transactionDate.toString(),
					dateSpent,
					historyDetails
				);
			})
			.collect(Collectors.toList());

		return StatisticsDetailResponseDto.builder()
			.categoryId(category.ordinal())
			.categoryName(category.name())
			.totalSpent(totalSpent)
			.transactions(transactionDetails)
			.build();
	}
}
