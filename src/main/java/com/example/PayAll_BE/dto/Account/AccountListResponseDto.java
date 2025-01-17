package com.example.PayAll_BE.dto.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
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
}
