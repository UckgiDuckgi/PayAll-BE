package com.example.PayAll_BE.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.dto.Account.AccountListResponseDto;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.Account;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.mapper.AccountMapper;
import com.example.PayAll_BE.repository.AccountRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	public AccountListResponseDto getUserAccounts(String token) {
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

		List<Account> accounts = accountRepository.findAllByUserId(user.getId());

		return AccountMapper.toAccountListResponseDto(user, accounts);
	}
}
