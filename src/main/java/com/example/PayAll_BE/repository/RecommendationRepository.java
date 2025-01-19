package com.example.PayAll_BE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.entity.Recommendation;
import com.example.PayAll_BE.entity.User;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
	List<Recommendation> findByUser(User user);
}
