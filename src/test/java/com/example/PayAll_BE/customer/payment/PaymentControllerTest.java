package com.example.PayAll_BE.customer.payment;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.PayAll_BE.customer.account.Account;
import com.example.PayAll_BE.customer.account.AccountRepository;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.enums.PaymentType;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetail;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetailRepository;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class PaymentControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private AuthService authService;
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

		this.accessToken = jwtService.generateAccessTestToken(testUser1.getAuthId(), testUser1.getId());
	}




}
