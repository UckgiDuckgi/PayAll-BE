package com.example.PayAll_BE.dto.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDto {
	private String bankName;
	private String accountName;
	private Long balance;
}
