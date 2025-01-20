package com.example.PayAll_BE.dto;

import java.math.BigDecimal;


import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreStatisticsDto {
	private String name;                // 카테고리 또는 가게 이름
	private String store;               // 가게 이름
	private Long totalSpent;            // 총 소비 금액
	private Long storePurchaseCount;    // 가게 방문 횟수
	private String type;                // CATEGORY 또는 STORE
}
