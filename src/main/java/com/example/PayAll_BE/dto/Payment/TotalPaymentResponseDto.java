package com.example.PayAll_BE.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalPaymentResponseDto {
	private Long totalBalance;
	private Long monthPaymentPrice;
	private List<DayPaymentResponseDto> paymentList;
}
