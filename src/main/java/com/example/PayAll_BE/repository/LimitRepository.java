package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PayAll_BE.entity.Limits;

public interface LimitRepository extends JpaRepository<Limits, Long> {

	Optional<Limits> findTopByUser_IdOrderByLimitDateDesc(Long userId);

	Optional<Limits> findFirstByUserIdAndLimitDateBetweenOrderByLimitDateDesc(
		Long userId, LocalDateTime startDate, LocalDateTime endDate
	);

	@Query("SELECT COUNT(l) > 0 FROM Limits l WHERE l.user.id = :userId AND YEAR(l.limitDate) = :year AND MONTH(l.limitDate) = :month")
	boolean existsByUserIdAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
}
