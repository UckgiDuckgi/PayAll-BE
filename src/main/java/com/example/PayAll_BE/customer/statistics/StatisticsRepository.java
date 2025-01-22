package com.example.PayAll_BE.customer.statistics;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.enums.Category;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
	List<Statistics> findByUserIdAndStatisticsDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

	Statistics findByUserAndCategoryAndStatisticsDate(User user, Category category, LocalDateTime statisticsDate);

	@Query("SELECT s FROM Statistics s WHERE s.user = :user AND s.statisticsDate BETWEEN :firstDayOfMonth AND :firstDayOfNextMonth")
	List<Statistics> findByUserAndThisMonth(@Param("user") User user, @Param("firstDayOfMonth") LocalDateTime firstDayOfMonth, @Param("firstDayOfNextMonth") LocalDateTime firstDayOfNextMonth);

}
