package com.example.PayAll_BE.customer.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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
import com.example.PayAll_BE.customer.benefit.Benefit;
import com.example.PayAll_BE.customer.benefit.BenefitRepository;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.enums.PaymentType;
import com.example.PayAll_BE.customer.enums.ProductType;
import com.example.PayAll_BE.customer.payment.Payment;
import com.example.PayAll_BE.customer.payment.PaymentRepository;
import com.example.PayAll_BE.customer.store.Store;
import com.example.PayAll_BE.customer.store.StoreRepository;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.JwtService;

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
public class ProductControllerTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private BenefitRepository benefitRepository;

	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountRepository accountRepository;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private JwtService jwtService;
	private User testUser1;
	private Account testAccount1;
	private String token;
	private  Long productId;

	@BeforeEach
	public void setUp() {
		entityManager.clear();
		// 테스트용 사용자 생성
		testUser1 = User.builder()
			.name("규호랑이")
			.authId("gyuhoTiger")
			.email("testuser1@example.com")
			.password("12345678")
			.build();
		userRepository.save(testUser1);

		// 테스트용 계좌 생성
		testAccount1 = Account.builder()
			.user(testUser1)
			.accountName("테스트계좌1")
			.bankName("하나은행")
			.accountNumber("111-1111-1111")
			.balance(100000L)
			.build();
		accountRepository.save(testAccount1);
		this.token = jwtService.generateAccessTestToken(testUser1.getAuthId(), testUser1.getId());

		Store store = Store.builder()
			.storeName("Test Store")
			.category(Category.CAFE)
			.build();
		storeRepository.save(store);

		Product product = Product.builder()
			.productName("Test Product")
			.productDescription("Description of the test product")
			.benefitDescription("Test product benefit")
			.productType(ProductType.CARD)
			.build();
		productRepository.save(product);

		productId = product.getId();

		Benefit benefit = Benefit.builder()
			.product(product)
			.store(store)
			.benefitValue(10L)
			.build();
		benefitRepository.save(benefit);

		Payment payment = Payment.builder()
			.account(testAccount1)
			.paymentPlace("Test Store")
			.price(100000L)
			.paymentTime(LocalDateTime.now())
			.paymentType(PaymentType.OFFLINE)
			.category(Category.CAFE)
			.build();
		paymentRepository.save(payment);
	}

	@Test
	@Order(1)
	void calculateBenefitWithValidAccessToken() throws Exception {
		mockMvc.perform(get("/api/product/{productId}", productId)
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())  // HTTP 상태 코드가 200 OK인지 확인
			.andExpect(jsonPath("$.status").value("OK"))  // 응답 JSON의 status 필드가 "OK"인지 확인
			.andExpect(jsonPath("$.message").value("추천 데이터 응답 성공"));  // 응답 메시지 확인
	}
}
