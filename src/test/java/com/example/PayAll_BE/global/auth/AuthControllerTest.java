package com.example.PayAll_BE.global.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private JwtService jwtService;

	@BeforeAll
	public void setUp() {
		entityManager.clear();
		// 테스트용 사용자 생성 ( 기존 사용자 )
		// testUser1 = User.builder()
		// 	.name("규호랑이")
		// 	.authId("gyuhoTiger")
		// 	.email("testuser1@example.com")
		// 	.password("12345678")
		// 	.permission(true)
		// 	.build();
		// userRepository.save(testUser1);
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
		// System.out.println("userRepository = " + userRepository.findAll());
	}

	@Test
	@Order(3)
	void noMydataLoginSuccess() throws Exception {
		// 사용자 첫 로그인 ( 마이데이터 연동 안된 상태)
		AuthRequestDto authDto = AuthRequestDto.builder()
			.authId("gyuhoLion")
			.password("password123")
			.build();

		mockMvc.perform(post("/api/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("mydata 연동이 되어있지 않습니다."));
	}

	@Test
	@Order(4)
	void loginFail_InvalidId() throws Exception {
		// 잘못된 인증 정보로 로그인 요청
		AuthRequestDto authDto = AuthRequestDto.builder()
			.authId("gyuTiger")
			.password("password123")
			.build();

		mockMvc.perform(post("/api/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authDto)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("아이디/이메일 또는 비밀번호를 잘못 입력하셨습니다."));
	}
	@Test
	@Order(5)
	void loginFail_InvalidPassword() throws Exception {
		// 잘못된 인증 정보로 로그인 요청
		AuthRequestDto authDto = AuthRequestDto.builder()
			.authId("gyuhoLion")
			.password("Invalidpassword")
			.build();

		mockMvc.perform(post("/api/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authDto)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("아이디/이메일 또는 비밀번호를 잘못 입력하셨습니다."));
	}

	@Test
	@Order(6)
	void refreshTokenSuccess() throws Exception {
		Optional<User> user = userRepository.findByAuthId("gyuhoLion");
		String refreshToken = jwtService.generateRefreshToken(user.get().getAuthId(), user.get().getId());
		// 1. 쿠키에 리프레시 토큰을 설정하여 API 호출
		mockMvc.perform(post("/api/auth/refresh")
				.cookie(new Cookie("refreshToken", refreshToken)))  // 리프레시 토큰 쿠키 설정
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("토큰 갱신 성공"))
			.andExpect(cookie().exists("refreshToken"))  // 갱신된 리프레시 토큰 확인
			.andExpect(cookie().exists("accessToken"));  // 갱신된 액세스 토큰 확인
	}

	@Test
	void setPlatform() {
	}

	@Test
	void getPlatform() {
	}

	@AfterAll
	public void AfterAll() {
		userRepository.deleteAll();
	}
}
