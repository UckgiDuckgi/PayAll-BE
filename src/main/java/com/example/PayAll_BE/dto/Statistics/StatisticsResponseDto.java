package com.example.PayAll_BE.dto.Statistics;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsResponseDto {
	private String date;
	private long totalSpent; // 총 지출
	private long dateAverage; // 하루 평균 지출
	private long difference; // 전월 대비 차이
	private List<CategoryExpense> categoryExpenses; // 카테고리별 지출
	private List<FixedExpense> fixedExpenses; // 고정 지출

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CategoryExpense {
		private int categoryId;
		private String category; // 카테고리 이름
		private long amount; // 지출 금액
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FixedExpense {
		private String fixedName; // 고정 지출 이름
		private long amount; // 지출 금액
		private String dueDate; // 결제일
	}
}
