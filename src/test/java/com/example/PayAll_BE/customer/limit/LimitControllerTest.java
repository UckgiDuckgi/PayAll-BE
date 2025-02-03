package com.example.PayAll_BE.customer.limit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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

import com.example.PayAll_BE.customer.limit.dto.LimitRegisterRequestDto;
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
public class LimitControllerTest {

	@Autowired
	private UserRepository userRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private LimitRepository limitRepository;

	private String token;
	private User testUser;

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
	void registerLimit_Success() throws Exception {
		LimitRegisterRequestDto requestDto = LimitRegisterRequestDto.builder()
			.limitPrice(500000L)
			.build();

		String requestBody = new ObjectMapper().writeValueAsString(requestDto);

		mockMvc.perform(post("/api/limit")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("소비 목표 등록 성공"));
	}

	@Test
	void registerLimit_Fail_AlreadyRegistered() throws Exception {
		limitRepository.save(Limits.builder()
			.user(testUser)
			.limitPrice(400000L)
			.limitDate(LocalDateTime.now())
			.build());

		LimitRegisterRequestDto requestDto = LimitRegisterRequestDto.builder()
			.limitPrice(500000L)
			.build();

		String requestBody = new ObjectMapper().writeValueAsString(requestDto);

		mockMvc.perform(post("/api/limit")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("이미 이번 달에 소비 목표가 등록되었습니다."));
	}

	@Test
	void getLimit_Success() throws Exception {
		mockMvc.perform(get("/api/limit")
				.cookie(new Cookie("accessToken", token))
				.param("yearMonth", "2025-02")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("소비 목표 조회 성공"));
	}
}
