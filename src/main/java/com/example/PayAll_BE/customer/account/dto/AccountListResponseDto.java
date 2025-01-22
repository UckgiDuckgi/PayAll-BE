package com.example.PayAll_BE.customer.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountListResponseDto {
	private String userName;
	private List<AccountResponseDto> accountList;
	private Long totalBalance;
	private Long PaymentDifference;
}
