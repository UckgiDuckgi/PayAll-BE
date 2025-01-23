package com.example.PayAll_BE.customer.payment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
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
import com.example.PayAll_BE.customer.payment.dto.PaymentUpdateRequest;
import com.example.PayAll_BE.customer.payment.dto.PaymentUpdateRequestDto;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetail;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetailRepository;
import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentListRequestDto;
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
public class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;
	@PersistenceContext
	private EntityManager entityManager;

	private String accessToken;
	private User testUser;
	private Account testAccount;
	private Payment testPayment;
	private PaymentDetail testPaymentDetail1;
	private PaymentDetail testPaymentDetail2;

	@BeforeEach
	public void setup(){
		entityManager.clear();
		testUser = User.builder()
			.name("TestUser")
			.authId("test99999")
			.password("password99999")
			.permission(true)
			.build();
		userRepository.save(testUser);

		testAccount = Account.builder()
			.user(testUser)
			.bankName("하나은행")
			.accountName("달달")
			.balance(1000000L)
			.accountNumber("111-111111-11-111")
			.build();
		accountRepository.save(testAccount);

		testPayment = Payment.builder()
			.account(testAccount)
			.paymentPlace("쿠팡")
			.price(25000L)
			.paymentType(PaymentType.ONLINE)
			.paymentTime(LocalDateTime.parse("2025-01-23T10:27:00"))
			.category(Category.SHOPPING)
			.build();
		paymentRepository.save(testPayment);

		testPaymentDetail1 = PaymentDetail.builder()
			.payment(testPayment)
			.productId(1L)
			.productName("빵부장 소금빵맛")
			.productPrice(1200L)
			.quantity(10)
			.build();

		testPaymentDetail2 = PaymentDetail.builder()
			.payment(testPayment)
			.productId(2L)
			.productName("버터와플")
			.productPrice(3200L)
			.quantity(2)
			.build();

		paymentDetailRepository.save(testPaymentDetail1);
		paymentDetailRepository.save(testPaymentDetail2);

		this.accessToken = jwtService.generateAccessTestToken(testUser.getAuthId(), testUser.getId());
	}

	@Test
	void getPayments() throws Exception {
		mockMvc.perform(get("/api/accounts/payments")
					.cookie(new Cookie("accessToken", accessToken))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.message").value("통합 계좌 거래 내역 조회 성공"))
			.andExpect(jsonPath("$.data.userName").value(testUser.getName()))
			.andExpect(jsonPath("$.data.paymentCount").value(1))
			.andExpect(jsonPath("$.data.paymentList[0].paymentDetail[0].productName").value("빵부장 소금빵맛"))  // 첫 번째 상세 내역 확인
			.andExpect(jsonPath("$.data.paymentList[0].paymentDetail[1].productName").value("버터와플"));  // 두 번째 상세 내역 확인
	}

	@Test
	void getPaymentDetail() throws Exception {
		mockMvc.perform(get("/api/accounts/payments/{paymentId}", testPayment.getId())
				.cookie(new Cookie("accessToken", accessToken))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.message").value("결제 상세 조회 성공"))
			.andExpect(jsonPath("$.data.paymentPlace").value("쿠팡"))
			.andExpect(jsonPath("$.data.paymentPrice").value(25000))
			.andExpect(jsonPath("$.data.paymentDetailList[0].productName").value("빵부장 소금빵맛"));
	}

	@Test
	void uploadPaymentDetail() throws Exception {

		PaymentListRequestDto.PaymentDetailInfoRequestDto.PurchaseProductRequestDto product1 =
			PaymentListRequestDto.PaymentDetailInfoRequestDto.PurchaseProductRequestDto.builder()
				.productName("아메리카노")
				.price(4000L)
				.amount(2)
				.build();

		PaymentListRequestDto.PaymentDetailInfoRequestDto.PurchaseProductRequestDto product2 =
			PaymentListRequestDto.PaymentDetailInfoRequestDto.PurchaseProductRequestDto.builder()
				.productName("촉촉한 카스테라")
				.price(4500L)
				.amount(1)
				.build();

		PaymentListRequestDto.PaymentDetailInfoRequestDto detailRequest =
			PaymentListRequestDto.PaymentDetailInfoRequestDto.builder()
				.paymentTime(LocalDateTime.now())
				.paymentPlace("스타벅스")
				.purchaseProductList(List.of(product1, product2))
				.build();

		PaymentListRequestDto requestDto = PaymentListRequestDto.builder()
			.paymentList(List.of(detailRequest))
			.build();


		mockMvc.perform(post("/api/accounts/payments/detail")
				.cookie(new Cookie("accessToken", accessToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.message").value("결제 내역 상세 업로드 성공")); // 메시지 확인
	}

	@Test
	void uploadPayments() throws Exception {
		PaymentUpdateRequestDto updateRequestDto1 = PaymentUpdateRequestDto.builder()
			.accountId(testAccount.getId())
			.paymentPlace("이마트")
			.paymentTime(LocalDateTime.now().minusDays(2))
			.build();

		PaymentUpdateRequestDto updateRequestDto2 = PaymentUpdateRequestDto.builder()
			.accountId(testAccount.getId())
			.paymentPlace("11번가")
			.paymentTime(LocalDateTime.now().minusDays(1))
			.build();

		PaymentUpdateRequest updateRequest = PaymentUpdateRequest.builder()
			.paymentList(List.of(updateRequestDto1, updateRequestDto2))
			.build();

		mockMvc.perform(patch("/api/accounts/payments")
				.cookie(new Cookie("accessToken", accessToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.message").value("결제처 업데이트가 완료되었습니다."));
	}

	@AfterAll
	public void AfterAll() {
		paymentDetailRepository.deleteAll();
		paymentRepository.deleteAll();
		accountRepository.deleteAll();
		userRepository.deleteAll();
	}
}
