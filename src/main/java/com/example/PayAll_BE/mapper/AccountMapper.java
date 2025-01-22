package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.customer.account.dto.AccountResponseDto;
import com.example.PayAll_BE.customer.account.dto.AccountListResponseDto;
import com.example.PayAll_BE.customer.account.Account;
import com.example.PayAll_BE.customer.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

	public static AccountResponseDto toAccountResponseDto(Account account) {
		return AccountResponseDto.builder()
			.accountId(account.getId())
			.bankName(account.getBankName())
			.accountName(account.getAccountName())
			.balance(account.getBalance())
			.accountId(account.getId())
			.build();
	}

	public static AccountListResponseDto toAccountListResponseDto(User user, List<Account> accounts, Long lastMonthTotalPaymentPrice, Long thisMonthTotalPaymentPrice) {
		List<AccountResponseDto> accountList = accounts.stream()
			.map(AccountMapper::toAccountResponseDto)
			.collect(Collectors.toList());

		Long totalBalance = accounts.stream()
			.mapToLong(Account::getBalance)
			.sum();

		return AccountListResponseDto.builder()
			.userName(user.getName())
			.accountList(accountList)
			.totalBalance(totalBalance)
			.PaymentDifference(lastMonthTotalPaymentPrice - thisMonthTotalPaymentPrice)
			.build();
	}
}
