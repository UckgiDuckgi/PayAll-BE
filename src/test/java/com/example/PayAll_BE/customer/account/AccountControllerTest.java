package com.example.PayAll_BE.customer.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
class AccountControllerTest {

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
	}

	@Test
	void getAccounts() throws Exception {
		mockMvc.perform(get("/api/accounts")
				.cookie(new Cookie("accessToken", token))  // 쿠키로 accessToken 전달
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())  // HTTP 상태 코드가 200 OK인지 확인
			.andExpect(jsonPath("$.status").value("OK"))  // 응답 JSON의 status 필드가 "OK"인지 확인
			.andExpect(jsonPath("$.message").value("사용자 계좌 목록 조회 성공"))  // 응답 메시지 확인
			.andExpect(jsonPath("$.data.accountList[0].accountId").value(testAccount1.getId()));
	}

	// @Test
	// void getAccounts_Unauthorized() throws Exception {
	// 	mockMvc.perform(get("/api/accounts")
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isForbidden());
	// }


	@AfterAll
	public void AfterAll() {
	}
}
