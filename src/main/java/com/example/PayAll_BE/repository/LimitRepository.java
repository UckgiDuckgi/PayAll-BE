package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.entity.Limits;

public interface LimitRepository extends JpaRepository<Limits, Long> {

	Optional<Limits> findTopByUser_IdOrderByLimitDateDesc(Long userId);

	Optional<Limits> findFirstByUserIdAndLimitDateBetweenOrderByLimitDateDesc(
		Long userId, LocalDateTime startDate, LocalDateTime endDate
	);
}
