package com.example.PayAll_BE.customer.receipt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.PayAll_BE.customer.account.Account;
import com.example.PayAll_BE.customer.account.AccountRepository;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.enums.PaymentType;
import com.example.PayAll_BE.customer.payment.Payment;
import com.example.PayAll_BE.customer.payment.PaymentRepository;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetail;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetailRepository;
import com.example.PayAll_BE.customer.receipt.dto.ReceiptRequestDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ReceiptControllerTest {

	@Autowired
	private UserRepository userRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService;

	private String token;
	private Account testAccount;
	private Payment testPayment1;
	private Payment testPayment2;
	private PaymentDetail testPaymentDetail1;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@BeforeEach
	public void setUp() {
		entityManager.clear();

		User testUser = User.builder()
			.name("테스트 유저")
			.authId("testUser")
			.email("testuser@example.com")
			.password("12345678")
			.build();
		userRepository.save(testUser);

		testAccount = Account.builder()
			.user(testUser)
			.bankName("테스트은행")
			.accountName("테스트계좌")
			.accountNumber("12345678")
			.balance(1000000L)
			.build();
		accountRepository.save(testAccount);

		testPayment1 = paymentRepository.save(Payment.builder()
			.account(testAccount)
			.paymentPlace("다이소")
			.price(8000L)
			.paymentTime(LocalDateTime.parse("2025-02-02T15:20:00"))
			.paymentType(PaymentType.OFFLINE)
			.category(Category.SHOPPING)
			.build());

		testPayment2 = paymentRepository.save(Payment.builder()
			.account(testAccount)
			.paymentPlace("밥플러스")
			.price(8000L)
			.paymentTime(LocalDateTime.parse("2025-02-02T15:20:00"))
			.paymentType(PaymentType.OFFLINE)
			.category(Category.RESTAURANT)
			.build());

		testPaymentDetail1 = paymentDetailRepository.save(PaymentDetail.builder()
			.payment(testPayment1)
			.productName("세제")
			.productPrice(4000L)
			.quantity(2)
			.build());

		entityManager.flush();
		entityManager.clear();

		testPayment1 = paymentRepository.findById(testPayment1.getId())
			.orElseThrow(() -> new RuntimeException("testPayment1 저장 실패"));
		testPayment2 = paymentRepository.findById(testPayment2.getId())
			.orElseThrow(() -> new RuntimeException("testPayment2 저장 실패"));

		this.token = jwtService.generateAccessTestToken(testUser.getAuthId(), testUser.getId());
	}


	@Test
	void uploadReceipt_Fail_AlreadyRegistered() throws Exception {
		ReceiptRequestDto receiptRequestDto = ReceiptRequestDto.builder()
			.paymentId(testPayment1.getId())
			.productList(List.of(
				ReceiptRequestDto.ReceiptDetailDto.builder()
					.productName("물티슈")
					.quantity(2)
					.price(1000L)
					.build()
			))
			.build();

		String requestBody = new ObjectMapper().writeValueAsString(receiptRequestDto);

		mockMvc.perform(post("/api/receipt")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("이미 영수증이 등록된 결제 내역입니다."));
	}

	@Test
	void uploadReceipt_Success() throws Exception {
		ReceiptRequestDto receiptRequestDto = ReceiptRequestDto.builder()
			.paymentId(testPayment2.getId())
			.productList(List.of(
				ReceiptRequestDto.ReceiptDetailDto.builder()
					.productName("물티슈")
					.quantity(2)
					.price(1000L)
					.build()
			))
			.build();

		String requestBody = new ObjectMapper().writeValueAsString(receiptRequestDto);

		mockMvc.perform(post("/api/receipt")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("영수증 업로드 성공"));
	}
}
