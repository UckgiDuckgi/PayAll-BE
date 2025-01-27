package com.example.PayAll_BE.customer.recommendation;

import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.PayAll_BE.customer.account.Account;
import com.example.PayAll_BE.customer.account.AccountRepository;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.enums.ProductType;
import com.example.PayAll_BE.customer.product.Product;
import com.example.PayAll_BE.customer.product.ProductRepository;
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
class RecommendationControllerTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private RecommendationRepository recommendationRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private JwtService jwtService;

	private User testUser1;
	private Account testAccount1;
	private String token;

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

		Product testProduct = Product.builder()
			.productName("테스트상품")
			.productType(ProductType.CARD)
			.productDescription("테스트 상품 설명")
			.build();

		productRepository.save(testProduct);

		Recommendation testRecommendation = Recommendation.builder()
			.user(testUser1)
			.storeName("테스트스토어")
			.visitCount(10L)
			.discountAmount(500L)
			.category(Category.LIVING)
			.dateTime(LocalDateTime.now())
			.product(testProduct)
			.productType(ProductType.CARD)
			.build();

		recommendationRepository.save(testRecommendation);
		this.token = jwtService.generateAccessTestToken(testUser1.getAuthId(), testUser1.getId());
	}

	@Test
	@Order(1)
	void recommendation() throws Exception {
		mockMvc.perform(get("/api/recommendations")
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("추천 데이터 응답 성공"));
	}

	@Test
	void getRecommendProducts() {
	}
}
