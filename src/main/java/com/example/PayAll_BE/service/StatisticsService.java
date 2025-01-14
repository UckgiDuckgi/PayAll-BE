package com.example.PayAll_BE.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Statistics.StatisticsResponseDto;
import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.repository.StatisticsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {
	private final StatisticsRepository statisticsRepository;

	public StatisticsResponseDto getStatistics(Long userId, String date) {

		LocalDate startDate = LocalDate.parse(date + "-01");
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = startDate.plusMonths(1).atStartOfDay().minusSeconds(1);

		List<Statistics> statistics = statisticsRepository.findByUserIdAndStatisticsDateBetween(1L, startDateTime, endDateTime);

		// 총 지출 계산
		long totalSpent = statistics.stream().mapToLong(Statistics::getStatisticsAmount).sum();

		// 카테고리별 지출 계산
		List<StatisticsResponseDto.CategoryExpense> categoryExpenses = statistics.stream()
			.collect(Collectors.groupingBy(
				Statistics::getStatisticsCategory,
				Collectors.summingLong(Statistics::getStatisticsAmount)
			))
			.entrySet().stream()
			.map(entry -> new StatisticsResponseDto.CategoryExpense(
				entry.getKey().ordinal(), // 카테고리 ID
				entry.getKey().getCategory(), // 카테고리 이름
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
			userId, previousStartDateTime, previousEndDateTime);
		long previousTotalSpent = previousStatistics.stream().mapToLong(Statistics::getStatisticsAmount).sum();
		long difference = totalSpent - previousTotalSpent;

		// ResponseDTO 생성
		return StatisticsResponseDto.builder()
			.date(date)
			.totalSpent(totalSpent)
			.dateAverage(dateAverage) // 하루 평균 지출
			.difference(difference) // 전월 대비 차이
			.categoryExpenses(categoryExpenses)
			.fixedExpenses(null) // 고정 지출(일단 비움)
			.build();
	}
}
