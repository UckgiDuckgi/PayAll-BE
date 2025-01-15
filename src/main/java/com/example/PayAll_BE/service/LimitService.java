package com.example.PayAll_BE.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Limit.LimitRequestDto;
import com.example.PayAll_BE.dto.Limit.LimitResponseDto;
import com.example.PayAll_BE.entity.Limit;
import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.repository.LimitRepository;
import com.example.PayAll_BE.repository.StatisticsRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LimitService {
	private final LimitRepository limitRepository;
	private final UserRepository userRepository;
	private final StatisticsRepository statisticsRepository;

	public LimitResponseDto registerLimit(Long userId, LimitRequestDto limitRequestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

		LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
		LocalDateTime now = LocalDateTime.now();
		long averageSpent = calculateAverageSpent(userId, threeMonthsAgo, now);

		Limit limit = Limit.builder()
			.user(user)
			.limitPrice(limitRequestDto.getLimitPrice())
			.limitDate(LocalDateTime.now())
			.build();

		Limit savedLimit = limitRepository.save(limit);

		return LimitResponseDto.builder()
			.limitId(savedLimit.getLimitId())
			.userId(userId)
			.limitPrice(savedLimit.getLimitPrice())
			.limitDate(savedLimit.getLimitDate())
			.averageSpent(averageSpent)
			.build();
	}

	public LimitResponseDto getLimit(Long userId) {
		// 가장 최근 소비 목표 조회
		Limit limit = limitRepository.findTopByUser_IdOrderByLimitDateDesc(userId)
			.orElseThrow(() -> new IllegalArgumentException("소비 목표가 존재하지 않습니다."));

		// 지난 3개월 평균 지출 계산
		LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
		LocalDateTime now = LocalDateTime.now();
		long averageSpent = calculateAverageSpent(userId, threeMonthsAgo, now);

		// 지난달 계산
		int lastMonth = now.minusMonths(1).getMonthValue();
		int lastMonthYear = now.minusMonths(1).getYear();

		// 지난달 소비 목표 조회
		Limit lastMonthLimit = limitRepository.findFirstByUserIdAndLimitDateBetweenOrderByLimitDateDesc(
			userId,
			LocalDateTime.of(lastMonthYear, lastMonth, 1, 0, 0),
			LocalDateTime.of(lastMonthYear, lastMonth, 1, 0, 0).plusMonths(1).minusSeconds(1)
		).orElse(null);

		Long lastMonthLimitPrice = lastMonthLimit != null ? lastMonthLimit.getLimitPrice() : null;

		return LimitResponseDto.builder()
			.limitId(limit.getLimitId())
			.userId(limit.getUser().getId())
			.limitPrice(limit.getLimitPrice())
			.limitDate(limit.getLimitDate())
			.averageSpent(averageSpent) // 평균 지출 추가
			.lastMonthLimit(lastMonthLimitPrice) // 지난달 소비 목표 금액
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
}
