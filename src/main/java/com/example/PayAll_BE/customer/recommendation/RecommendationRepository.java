package com.example.PayAll_BE.customer.recommendation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.customer.user.User;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
	List<Recommendation> findByUser(Optional<User> user);
	boolean existsRecommendationByDateTimeAndUser(LocalDateTime dateTime,User user);
}
