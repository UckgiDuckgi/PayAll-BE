package com.example.PayAll_BE.dto.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDto {
	private Long accountId;
	private String bankName;
	private String accountName;
	private Long balance;
	private Long accountId;
}
