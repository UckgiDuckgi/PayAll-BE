package com.example.PayAll_BE.customer.payment;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.customer.account.Account;
import com.example.PayAll_BE.customer.account.AccountRepository;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.enums.PaymentType;
import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentListRequestDto;

import com.example.PayAll_BE.customer.paymentDetails.PaymentDetailRepository;

import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductApiClient;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService;

	@PersistenceContext
	private EntityManager entityManager;

	private String token;
	private User testUser;
	private Account testAccount;
	private Payment testPayment;

	@MockitoBean
	private CrawlingProductApiClient crawlingProductApiClient;

	@BeforeEach
	public void setUp() {
		entityManager.clear();

		testUser = User.builder()
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

		LocalDateTime fixedPaymentTime = LocalDateTime.now().withNano(0);

		testPayment = paymentRepository.save(Payment.builder()
			.account(testAccount)
			.paymentPlace("Coupang")
			.price(3170L)
			.paymentTime(fixedPaymentTime)
			.paymentType(PaymentType.ONLINE)
			.category(Category.SHOPPING)
			.build());

		this.token = jwtService.generateAccessTestToken(testUser.getAuthId(), testUser.getId());
	}
	@Test
	void getPayments_Success() throws Exception {
		mockMvc.perform(get("/api/accounts/payments")
				.cookie(new Cookie("accessToken", token))
				.param("accountId", String.valueOf(testAccount.getId()))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("통합 계좌 거래 내역 조회 성공"))
			.andExpect(jsonPath("$.data.paymentList[*].paymentDetail[*].paymentPlace", hasItem("Coupang")));
	}

	@Test
	void getPaymentDetail_Success() throws Exception {
		mockMvc.perform(get("/api/accounts/payments/" + testPayment.getId())
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("결제 상세 조회 성공"))
			.andExpect(jsonPath("$.data.paymentPlace").value("Coupang"));
	}

	@Test
	void getPaymentDetail_Fail_NotFound() throws Exception {
		mockMvc.perform(get("/api/accounts/payments/99999")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("결제 내역을 찾을 수 없습니다."));
	}

	@Test
	void uploadPaymentDetail_Success() throws Exception {
		CrawlingProductDto productDto = CrawlingProductDto.builder()
			.pCode(1026291L)
			.productName("신라면 (5개)")
			.productImage("https://img.danawa.com/prod_img/500000")
			.shopName("Coupang")
			.shopUrl("https://www.coupang.com/vp/products/7958974")
			.price(3170L)
			.build();

		long fixedPaymentTimeMillis = testPayment.getPaymentTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();

		PaymentListRequestDto requestDto = PaymentListRequestDto.builder()
			.paymentList(List.of(
				PaymentListRequestDto.PaymentDetailInfoRequestDto.builder()
					.paymentTime(fixedPaymentTimeMillis)
					.paymentPlace("Coupang")
					.purchaseProductList(List.of(
						PaymentListRequestDto.PaymentDetailInfoRequestDto.PurchaseProductRequestDto.builder()
							.productName("신라면 (5개)")
							.price(3170L)
							.amount(1)
							.build()
					))
					.build()
			))
			.build();

		when(crawlingProductApiClient.fetchProductByName("신라면 (5개)")).thenReturn(productDto);

		String requestBody = objectMapper.writeValueAsString(requestDto);

		mockMvc.perform(post("/api/accounts/payments/details")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("결제 내역 상세 업로드 성공"))
			.andDo(print());

		verify(crawlingProductApiClient).fetchProductByName("신라면 (5개)");
	}


}
