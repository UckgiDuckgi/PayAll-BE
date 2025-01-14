package com.example.PayAll_BE.dto.Limit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LimitRequestDto {
	private long averageSpent; // 지난 3개월 평균 지출 금액
	private long limitPrice;
}
