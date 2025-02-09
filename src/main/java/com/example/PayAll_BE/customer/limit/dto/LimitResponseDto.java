package com.example.PayAll_BE.customer.limit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LimitResponseDto {
	private Long limitId;
	private Long userId;
	private Long limitPrice;
	private Long spentAmount; // 현재 소비 금액
	private Long savedAmount; // 절약 금액 <<<< 반드시 로직 수정해야함 지금 일단 임시방편임
	private Long averageSpent; // 지난 3개월 평균 소비 금액
	private Long lastMonthLimit; // 지난달 소비 목표 금액
	private LocalDate startDate; // 소비 목표 기간 시작 날짜
	private LocalDate endDate; // 소비 목표 기간 종료 날짜
}
