package com.example.PayAll_BE.customer.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticsDiffResponseDto {
	private String userName;
	private Long yearlySavingAmount;
	private Long monthlyPaymentDifference;
}
