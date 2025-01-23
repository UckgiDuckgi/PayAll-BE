package com.example.PayAll_BE.global.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.dto.AuthRequestDto;
import com.example.PayAll_BE.global.auth.dto.RegisterRequestDto;
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
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@PersistenceContext
	private EntityManager entityManager;
	private User testUser1;
	private User testUser2;

	@BeforeEach
	public void setUp() {
		entityManager.clear();
		// 테스트용 사용자 생성
		testUser1 = User.builder()
			.name("규호랑이")
			.authId("gyuhoTiger")
			.email("testuser1@example.com")
			.password("12345678")
			.permission(true)
			.build();
		userRepository.save(testUser1);

		testUser2 = User.builder()
			.name("규호랑이2")
			.authId("gyuhoTiger2")
			.email("testuser1@example.com")
			.password("12345678")
			.build();
		userRepository.save(testUser2);
	}
	@Test
	@Order(1)
	void registerSuccess() throws Exception {
		// 정상적인 회원가입 요청
		RegisterRequestDto requestDto = RegisterRequestDto.builder()
			.name("규사자")
			.authId("gyuhoLion")
			.password("password123")
			.phone("010-1234-5678")
			.address("서울시 강남구")
			.email("gyuho@example.com")
			.build();

		mockMvc.perform(post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));
	}

	@Test
	@Order(2)
	void registerFail_NoPassword() throws Exception {
		// 비밀번호가 없는 경우 회원가입 실패 테스트
		RegisterRequestDto requestDto = RegisterRequestDto.builder()
			.name("규사자")
			.authId("gyuhoLion")
			.password("")  // 비밀번호를 빈 값으로 설정
			.phone("010-1234-5678")
			.address("서울시 강남구")
			.email("gyuho@example.com")
			.build();

		mockMvc.perform(post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("올바른 비밀번호를 입력해주세요."));
		System.out.println("userRepository = " + userRepository.findAll());
	}

	// @Test
	// @Order(3)
	// void noMydataLoginSuccess() throws Exception {
	// 	// 사용자 첫 로그인 ( 마이데이터 연동 안된 상태)
	// 	AuthRequestDto authDto = AuthRequestDto.builder()
	// 		.authId("gyuhoTiger2")
	// 		.password("password123")
	// 		.build();
	//
	// 	mockMvc.perform(post("/api/auth/sign-in")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(authDto)))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("OK"))
	// 		.andExpect(jsonPath("$.message").value("mydata 연동이 되어있지 않습니다."));
	// }
	//
	// @Test
	// @Order(3)
	// void loginFail_InvalidCredentials() throws Exception {
	// 	// 잘못된 인증 정보로 로그인 요청
	// 	AuthRequestDto authDto = AuthRequestDto.builder()
	// 		.authId("gyuhoTiger")
	// 		.password("wrongPassword")
	// 		.build();
	//
	// 	mockMvc.perform(post("/api/auth/sign-in")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(authDto)))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("OK"))
	// 		.andExpect(jsonPath("$.message").value("mydata 연동이 되어있지 않습니다."));
	// }
	//
	// @Test
	// @Order(5)
	// void loginFail_NoAuthId() throws Exception {
	// 	// 존재하지 않는 authId로 로그인 요청
	// 	AuthRequestDto authDto = AuthRequestDto.builder()
	// 		.authId("nonexistentUser")
	// 		.password("password123")
	// 		.build();
	//
	// 	mockMvc.perform(post("/api/auth/sign-in")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(authDto)))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("OK"))
	// 		.andExpect(jsonPath("$.message").value("mydata 연동이 되어있지 않습니다."));
	// }

	@Test
	void refreshToken() {
	}

	@Test
	void setPlatform() {
	}

	@Test
	void getPlatform() {
	}
}
