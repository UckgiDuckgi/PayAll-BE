package com.example.PayAll_BE.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.example.PayAll_BE.entity.enums.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalPaymentResponseDto {
	private String userName;
	private Long totalBalance;
	private Long monthPaymentPrice;
	private List<DayPaymentResponseDto> paymentList;
	private String bankName;
	private String accountName;
	private String accountNumber;
	private Integer paymentCount;
	private Category category;
}
