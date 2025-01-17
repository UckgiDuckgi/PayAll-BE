package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
