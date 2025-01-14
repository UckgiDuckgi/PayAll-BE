package com.example.PayAll_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.entity.Limit;

public interface LimitRepository extends JpaRepository<Limit, Long> {
}
