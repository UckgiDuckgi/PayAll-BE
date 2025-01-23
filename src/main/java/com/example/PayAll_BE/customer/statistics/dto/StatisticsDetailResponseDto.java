package com.example.PayAll_BE.customer.statistics.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsDetailResponseDto {
	private int categoryId;
	private String categoryName;
	private long totalSpent; // 해당 카테고리 총 소비 금액
	private List<TransactionDetail> transactions; // 거래 내역 리스트

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TransactionDetail {
		private String date;
		private long dateSpent; // 해당 날짜 소비 금액
		private List<HistoryDetail> history; // 세부 내역 리스트

		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class HistoryDetail {
			private String store; // 가게 이름
			private String tag; // 태그
			private long amount;
			private String paymentType; // 결제 유형 (예: 오프라인 결제)
			private String time;
		}
	}
}
