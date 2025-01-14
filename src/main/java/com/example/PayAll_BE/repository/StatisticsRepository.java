package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.entity.Statistics;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
	List<Statistics> findByUserIdAndStatisticsDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
