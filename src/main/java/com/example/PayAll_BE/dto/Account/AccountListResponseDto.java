package com.example.PayAll_BE.dto.Account;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AccountListResponseDto {
	private String userName;
	private List<AccountResponseDto> accountList;
	private Long totalBalance;
}
