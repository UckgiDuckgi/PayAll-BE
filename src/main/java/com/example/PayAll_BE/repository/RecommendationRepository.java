package com.example.PayAll_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.entity.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
}
