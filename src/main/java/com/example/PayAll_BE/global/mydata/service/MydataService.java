package com.example.PayAll_BE.global.mydata.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.customer.account.Account;
import com.example.PayAll_BE.customer.account.AccountRepository;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.enums.PaymentType;
import com.example.PayAll_BE.customer.payment.Payment;
import com.example.PayAll_BE.customer.payment.PaymentRepository;
import com.example.PayAll_BE.customer.purchase.PurchaseRequestDto;
import com.example.PayAll_BE.customer.purchase.UpdateTransactionRequestDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.global.exception.BadRequestException;
import com.example.PayAll_BE.global.exception.NotFoundException;
import com.example.PayAll_BE.global.mydata.controller.MydataController;
import com.example.PayAll_BE.global.mydata.dto.AccountListResponseDto;
import com.example.PayAll_BE.global.mydata.dto.AccountRequestDto;
import com.example.PayAll_BE.global.mydata.dto.AccountResponseDto;
import com.example.PayAll_BE.global.mydata.dto.TransactionRequestDto;
import com.example.PayAll_BE.global.mydata.dto.TransactionResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MydataService {
	private final RestTemplate restTemplate;
	private final MydataController mydataController;
	private final AccountRepository accountRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final CategoryService categoryService;
	private final JwtService jwtService;

	@Value("${server1.base-url}")
	private String baseUrl;

	private static final Map<String, String> STORE = Map.of(
		"Coupang", "쿠팡",
		"11st", "11번가"
	);

	public String syncPurchaseData(String token, PurchaseRequestDto purchaseRequestDto) {
		List<UpdateTransactionRequestDto> transactionRequestDtos = purchaseRequestDto.getPurchaseList().stream()
			.map(product -> UpdateTransactionRequestDto.builder()
				.storeName(STORE.getOrDefault(product.getStoreName(), product.getStoreName()))
				.price(product.getProductPrice() * product.getQuantity())
				.build())
			.toList();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<List<UpdateTransactionRequestDto>> entity = new HttpEntity<>(
			transactionRequestDtos, headers);

		String url = baseUrl + "/api/accounts/purchase";

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
			log.info("거래 내역 마이데이터에 저장 성공");
			return response.getBody();
		} catch (Exception e) {
			throw new RuntimeException("마이데이터 동기화 실패");
		}

	}

	public void syncMydataInfo(String token) {
		Long userId = jwtService.extractUserId(token);

		// 계좌 목록 조회 호출
		ResponseEntity<AccountListResponseDto> accountList = mydataController.loadMydataAccountList(token);
		if (accountList.getBody() != null) {
			accountList.getBody().getAccountList().forEach(accountDto -> {
				Account checkAccount = accountRepository.findByUserIdAndAccountNumber(userId,
					accountDto.getAccountNum()).orElse(null);
				// 새로운 account이면 db 저장
				if (checkAccount == null) {
					User user = userRepository.findById(userId)
						.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));
					Account account = Account.builder()
						.user(user)
						.bankName(getBankNameByAccountNum(accountDto.getAccountNum()))
						.accountName(accountDto.getAccountName())
						.accountNumber(accountDto.getAccountNum())
						.balance(0L)   // 처음엔 0으로 해두고 계좌 정보 조회 호출해서 update
						.build();
					accountRepository.save(account);

					syncAccountBasicInfo(accountDto.getAccountNum(), userId);
				} else {
					// 기존 계좌가 있다면 balance만 update
					syncAccountBasicInfo(accountDto.getAccountNum(), userId);
				}

				// 거래 내역 호출 및 update
				String fromDate = accountList.getBody().getSearchTimestamp();
				System.out.println("fromDate = " + fromDate);
				syncAccountTransactions(accountDto.getAccountNum(), fromDate, userId);
			});

		}
	}

	// 계좌 정보 조회 호출 -> balance update
	private void syncAccountBasicInfo(String accountNum, Long userId) {

		AccountRequestDto requestDto = AccountRequestDto.builder()
			.orgCode("00001")
			.accountNum(accountNum)
			.searchTimestamp("0")
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

	// 계좌 거래내역 조회 호출 -> payment db update
	private void syncAccountTransactions(String accountNum, String fromDate, Long userId) {

		LocalDateTime fromDateTime;
		if ("0".equals(fromDate)) {
			// 최초 조회인 경우 180일 전부터 조회
			fromDateTime = LocalDateTime.now().minusDays(180);
		} else {
			try {
				fromDateTime = LocalDateTime.parse(fromDate,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
			} catch (DateTimeParseException e) {
				throw new RuntimeException("Invalid date format: " + fromDate);
			}
		}

		TransactionRequestDto requestDto = TransactionRequestDto.builder()
			.orgCode("00001")
			.accountNum(accountNum)
			.fromDate(Timestamp.valueOf(fromDateTime))
			.toDate(Timestamp.valueOf(LocalDateTime.now()))
			.build();

		System.out.println("조회 요청: requestDto.getFromDate() = " + requestDto.getFromDate());

		Account account = accountRepository.findByUserIdAndAccountNumber(userId, accountNum)
			.orElseThrow(() -> new NotFoundException("해당 계좌를 찾을 수 없습니다."));

		ResponseEntity<TransactionResponseDto> transactions = mydataController.getAccountTransactions(
			requestDto);
		if (transactions.getBody() != null) {
			transactions.getBody().getTransList().forEach(dto -> {
				boolean checkPayment = paymentRepository.existsByAccountIdAndPaymentTimeAndPriceAndPaymentPlace(
					account.getId(), dto.getTransDtime().toLocalDateTime(), dto.getTransAmt().longValue(),
					dto.getProdName());

				// 새로운 payment이면 db에 저장
				if (!checkPayment) {
					Payment payment = Payment.builder()
						.account(account)
						.paymentPlace(dto.getProdName())
						.price(dto.getTransAmt().longValue())
						.paymentTime(dto.getTransDtime().toLocalDateTime())
						.paymentType(PaymentType.valueOf(dto.getProdCode()))
						.category(getCategory(dto.getTransType(), dto.getProdCode(), dto.getProdName()))
						.build();

					paymentRepository.save(payment);
				}
			});
		}
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

	// 카테고리 분류 로직
	private Category getCategory(String transType, String prodCode, String prodName) {
		if (!"301".equals(transType) && !"401".equals(transType)) {
			throw new BadRequestException("Invalid transaction type: " + transType);
		}

		// 301(입금), 401(출금)
		return switch (transType) {
			case "301" -> Category.INCOME;
			case "401" -> switch (prodCode) {
				case "ONLINE", "PAYALL" -> Category.SHOPPING;
				case "OFFLINE" -> categoryService.getCategory(prodName);
				default -> Category.OTHERS;
			};
			default -> Category.OTHERS;
		};
	}
}
