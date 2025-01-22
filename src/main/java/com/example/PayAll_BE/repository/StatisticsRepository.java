package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.enums.Category;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
	List<Statistics> findByUserIdAndStatisticsDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

	Statistics findByUserAndCategoryAndStatisticsDate(User user, Category category, LocalDateTime statisticsDate);

	@Query("SELECT s FROM Statistics s WHERE s.user = :user AND s.statisticsDate BETWEEN :firstDayOfMonth AND :firstDayOfNextMonth")
	List<Statistics> findByUserAndThisMonth(@Param("user") User user,
		@Param("firstDayOfMonth") LocalDateTime firstDayOfMonth,
		@Param("firstDayOfNextMonth") LocalDateTime firstDayOfNextMonth);

	Optional<Statistics> findByUserIdAndCategoryAndStatisticsDate(Long userId, Category category,
		LocalDateTime statisticsDate);
}
