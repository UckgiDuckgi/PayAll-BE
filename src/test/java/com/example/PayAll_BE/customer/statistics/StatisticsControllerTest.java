package com.example.PayAll_BE.customer.statistics;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.MethodOrderer;
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
class StatisticsControllerTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StatisticsService statisticsService;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService;

	private User testUser;
	private String token;

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

		this.token = jwtService.generateAccessTestToken(testUser.getAuthId(), testUser.getId());
	}

	@Test
	void getStatistics() throws Exception {
		mockMvc.perform(get("/api/statistics")
				.cookie(new Cookie("accessToken", token))
				.param("date", "2024-01")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("소비분석 조회 성공"));
	}

	@Test
	void getStatisticsDetails() throws Exception {
		mockMvc.perform(get("/api/statistics/SHOPPING") // 카테고리 예시) SHOPPING
				.cookie(new Cookie("accessToken", token))
				.param("date", "2024-01")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("카테고리별 소비 분석 상세 조회 성공"));
	}

	@Test
	void getStatisticsDiff() throws Exception {
		mockMvc.perform(get("/api/statistics/diff")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("금액 차이 조회 성공"));
	}
}
