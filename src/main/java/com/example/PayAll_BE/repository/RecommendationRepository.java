package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.entity.Recommendation;
import com.example.PayAll_BE.entity.User;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
	List<Recommendation> findByUser(User user);
	boolean existsRecommendationByDateTimeAndUser(LocalDateTime dateTime,User user);
}
