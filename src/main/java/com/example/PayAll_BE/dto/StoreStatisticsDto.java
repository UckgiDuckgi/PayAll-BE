package com.example.PayAll_BE.dto;

import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.entity.enums.StatisticsCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreStatisticsDto {
	private Category category;
	private Long totalSpent;
	private String paymentPlace;
	private Long storeTotalSpent;
	private Long storePurchaseCount;
}
