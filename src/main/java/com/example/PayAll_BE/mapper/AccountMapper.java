package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.Account.AccountResponseDto;
import com.example.PayAll_BE.dto.Account.AccountListResponseDto;
import com.example.PayAll_BE.entity.Account;

import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

	public static AccountResponseDto toAccountResponseDto(Account account) {
		return AccountResponseDto.builder()
			.bankName(account.getBankName())
			.accountName(account.getAccountName())
			.balance(account.getBalance())
			.build();
	}

	public static AccountListResponseDto toAccountListResponseDto(String userName, List<Account> accounts) {
		List<AccountResponseDto> accountList = accounts.stream()
			.map(AccountMapper::toAccountResponseDto)
			.collect(Collectors.toList());

		Long totalBalance = accounts.stream()
			.mapToLong(Account::getBalance)
			.sum();

		return AccountListResponseDto.builder()
			.userName(userName)
			.accountList(accountList)
			.totalBalance(totalBalance)
			.build();
	}
}
