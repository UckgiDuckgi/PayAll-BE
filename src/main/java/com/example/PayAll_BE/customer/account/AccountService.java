package com.example.PayAll_BE.customer.account;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.customer.account.dto.AccountListResponseDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.mapper.AccountMapper;
import com.example.PayAll_BE.customer.payment.PaymentRepository;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final JwtService jwtService;

	public AccountListResponseDto getUserAccounts(String token) {
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

		List<Account> accounts = accountRepository.findAllByUserId(user.getId());
		List<Long> accountIds = accounts.stream()
			.map(Account::getId)
			.toList();

		YearMonth thisMonth = YearMonth.now();
		LocalDateTime thisMonthStart = thisMonth.atDay(1).atStartOfDay();
		LocalDateTime thisMonthEnd = thisMonth.atEndOfMonth().atTime(23, 59, 59);

		YearMonth lastMonth = thisMonth.minusMonths(1);
		LocalDateTime lastMonthStart = lastMonth.atDay(1).atStartOfDay();
		LocalDateTime lastMonthEnd = lastMonth.atEndOfMonth().atTime(23, 59, 59);

		Long lastMonthTotalPaymentPrice = paymentRepository.findTotalPaymentByAccountIdsAndDateRange(accountIds, lastMonthStart, lastMonthEnd);
		Long thisMonthTotalPaymentPrice = paymentRepository.findTotalPaymentByAccountIdsAndDateRange(accountIds, thisMonthStart, thisMonthEnd);

		lastMonthTotalPaymentPrice = lastMonthTotalPaymentPrice != null ? lastMonthTotalPaymentPrice : 0L;
		thisMonthTotalPaymentPrice = thisMonthTotalPaymentPrice != null ? thisMonthTotalPaymentPrice : 0L;

		return AccountMapper.toAccountListResponseDto(user, accounts, lastMonthTotalPaymentPrice, thisMonthTotalPaymentPrice);
	}
}
