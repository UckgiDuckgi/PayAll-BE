package com.example.PayAll_BE.global.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterAll;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.PayAll_BE.customer.account.AccountRepository;
import com.example.PayAll_BE.customer.cart.CartRepository;
import com.example.PayAll_BE.customer.limit.LimitRepository;
import com.example.PayAll_BE.customer.payment.PaymentRepository;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetailRepository;
import com.example.PayAll_BE.customer.recommendation.RecommendationRepository;
import com.example.PayAll_BE.customer.statistics.StatisticsRepository;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.dto.AuthRequestDto;
import com.example.PayAll_BE.global.auth.dto.PlatformRequestDto;
import com.example.PayAll_BE.global.auth.dto.RegisterRequestDto;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.global.auth.service.RedisService;
import com.example.PayAll_BE.global.config.security.CryptoUtil;
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
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private StatisticsRepository statisticsRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	PaymentDetailRepository paymentDetailRepository;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	RecommendationRepository recommendationRepository;
	@Autowired
	LimitRepository limitRepository;
	@Autowired
	RedisService redisService;
	@PersistenceContext
	private EntityManager entityManager;
	private User testUser1;
	private User testUser2;
	private String token1;
	private String token2;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private AccountRepository accountRepository;

	@BeforeEach
	public void setUp() {
		entityManager.clear();
		// 테스트용 사용자 생성

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

	// @Test
	// @Order(6)
	// void refreshTokenSuccess() throws Exception {
	// 	Optional<User> user = userRepository.findByAuthId("gyuhoLion");
	// 	String refreshToken = jwtService.generateRefreshTestToken(user.get().getAuthId(), user.get().getId());
	// 	// 1. 쿠키에 리프레시 토큰을 설정하여 API 호출
	// 	mockMvc.perform(post("/api/auth/refresh")
	// 			.cookie(new Cookie("refreshToken", refreshToken)))  // 리프레시 토큰 쿠키 설정
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("OK"))
	// 		.andExpect(jsonPath("$.message").value("토큰 갱신 성공"))
	// 		.andExpect(cookie().exists("refreshToken"))  // 갱신된 리프레시 토큰 확인
	// 		.andExpect(cookie().exists("accessToken"))  // 갱신된 액세스 토큰 확인
	// 		.andDo(print());
	//
	// }

	@Test
	@Order(7)
	void setPlatform() throws Exception {
		testUser1 = User.builder()
			.name("규호랑이")
			.authId("gyuhoTiger1")
			.email("testuser11@example.com")
			.password("12345678")
			.permission(true)
			.build();
		userRepository.save(testUser1);
		this.token1 = jwtService.generateAccessTestToken(testUser1.getAuthId(), testUser1.getId());

		PlatformRequestDto requestDto = PlatformRequestDto.builder()
			.platformName("Coupang")
			.id("hanaro@hanaro.com")
			.password("hanaro").build();

		mockMvc.perform(post("/api/auth/platform")
				.cookie(new Cookie("accessToken", token1))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("플랫폼 계정 등록 성공"))
			.andDo(print());

	}

	@Test
	@Order(8)
	void setPlatformWithInvalidPlatform() throws Exception {
		testUser1 = User.builder()
			.name("규호랑이11")
			.authId("gyuhoTiger1111")
			.email("testuser1111@example.com")
			.password("12345678")
			.permission(true)
			.build();
		userRepository.save(testUser1);
		this.token1 = jwtService.generateAccessTestToken(testUser1.getAuthId(), testUser1.getId());

		PlatformRequestDto requestDto = PlatformRequestDto.builder()
			.platformName("NAVVER")
			.id("hanaro@navver.com")
			.password("hanaro").build();

		mockMvc.perform(post("/api/auth/platform")
				.cookie(new Cookie("accessToken", token1))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("유효하지 않은 플랫폼입니다: NAVVER"))
			.andDo(print());

	}

	@Test
	@Order(9)
	void getPlatform() throws Exception {
		testUser2 = User.builder()
			.name("규호랑이2")
			.authId("gyuhoTiger2")
			.email("testuser22@example.com")
			.password("12345678")
			.elevenstId(CryptoUtil.encrypt("hanaro@hanaro.com"))
			.elevenstPassword(CryptoUtil.encrypt("hanaro"))
			.build();
		userRepository.save(testUser2);
		this.token2 = jwtService.generateAccessTestToken(testUser2.getAuthId(), testUser2.getId());

		mockMvc.perform(get("/api/auth/platform")
				.cookie(new Cookie("accessToken", token2)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("플랫폼 계정 조회 성공"))
			.andExpect(jsonPath("$.data.platformInfos[0].platformName").value("11ST"))
			.andDo(print());

	}

	@Test
	@Order(6)
	@WithMockUser
	void refreshTokenSuccess() throws Exception {
		User testUser1 = User.builder()
			.name("규호랑이")
			.authId("gyuhoLion")
			.email("testuser@example.com")
			.password(CryptoUtil.encrypt("password123"))
			.permission(true)
			.build();
		userRepository.save(testUser1);

		String refreshToken = jwtService.generateRefreshTestToken(testUser1.getAuthId(), testUser1.getId());

		redisService.saveRefreshToken(testUser1.getAuthId(), refreshToken, 3600L);

		mockMvc.perform(post("/api/auth/refresh")
				.cookie(new Cookie("refreshToken", refreshToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("토큰 갱신 성공"))
			.andExpect(cookie().exists("refreshToken"))
			.andExpect(cookie().exists("accessToken"))
			.andDo(print());
	}


	@AfterAll
	public void AfterAll() {
		paymentDetailRepository.deleteAll();
		paymentRepository.deleteAll();
		recommendationRepository.deleteAll();
		cartRepository.deleteAll();
		statisticsRepository.deleteAll();
		limitRepository.deleteAll();
		accountRepository.deleteAll();
		userRepository.deleteAll();
	}
}
