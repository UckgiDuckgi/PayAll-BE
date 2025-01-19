package com.example.PayAll_BE.mydata.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.AccountDto;
import com.example.PayAll_BE.entity.Account;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.BadRequestException;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.mydata.controller.MydataController;
import com.example.PayAll_BE.mydata.dto.AccountListResponseDto;
import com.example.PayAll_BE.mydata.dto.AccountRequestDto;
import com.example.PayAll_BE.mydata.dto.AccountResponseDto;
import com.example.PayAll_BE.repository.AccountRepository;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MydataService {
	private final MydataController mydataController;
	private final AccountRepository accountRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;

	public Object getAccountList(String authorization, String transactionId, String apiType, String orgCode,
		String searchTimestamp, String nextPage, int limit) {
		return null;
	}

	public void syncMydataInfo(Long userId) {
		// 계좌 목록 조회 호출
		ResponseEntity<AccountListResponseDto> accountList = mydataController.loadMydataAccountList();
		if (accountList.getBody() != null) {
			saveAccount(accountList.getBody().getAccountList(), userId);
			accountList.getBody()
				.getAccountList()
				.forEach(account -> syncAccountBasicInfo(account.getAccountNum(), userId));
		}
	}

	// 계좌 정보 조회 호출 -> balance update
	private void syncAccountBasicInfo(String accountNum, Long userId) {
		AccountRequestDto requestDto = AccountRequestDto.builder()
			.orgCode("00001")
			.accountNum(accountNum)
			.searchTimestamp(LocalDate.now().toString())   // todo. ????
			.build();

		ResponseEntity<AccountResponseDto> accountBasicInfo = mydataController.getAccountBasicInfo(requestDto);
		if (accountBasicInfo.getBody() != null) {
			Account account = accountRepository.findByUserIdAndAccountNumber(userId, accountNum)
				.orElseThrow(() -> new NotFoundException("해당 계좌를 찾을 수 없습니다."));
			Long balance = accountBasicInfo.getBody().getBasicList().get(0).getAvailBalance().longValue();
			account.setBalance(balance);
			accountRepository.save(account);
		}
	}

	// account db 저장
	private void saveAccount(List<AccountDto> accounts, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));
		List<Account> accountList = accounts.stream()
			.map(account -> Account.builder()
				.user(user)
				.bankName(getBankNameByAccountNum(account.getAccountNum()))
				.accountName(account.getAccountName())
				.accountNumber(account.getAccountNum())
				.balance(0L)   // 처음엔 0으로 해두고 계좌 정보 조회 호출해서 update
				.build())
			.toList();

		accountRepository.saveAll(accountList);
	}

	// 계좌번호 앞 3자리 기준으로 은행명 조회
	private String getBankNameByAccountNum(String accountNum) {
		if (accountNum == null || accountNum.length() < 3) {
			throw new BadRequestException("유효하지 않은 계좌번호입니다.");
		}

		String backCode = accountNum.substring(0, 3);
		return switch (backCode) {
			case "081" -> "하나은행";
			case "090" -> "카카오뱅크";
			case "012" -> "국민은행";
			case "140" -> "신한은행";
			case "020" -> "우리은행";
			case "003" -> "기업은행";
			case "011" -> "농협은행";
			case "051" -> "도이치뱅크";
			case "050" -> "제일은행";
			case "070" -> "토스뱅크";
			default -> throw new BadRequestException("유효하지 않은 계좌번호입니다.");
		};
	}
}
