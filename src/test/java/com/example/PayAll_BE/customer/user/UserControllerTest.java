package com.example.PayAll_BE.customer.user;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.customer.user.dto.UserResponseDto;

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
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@PersistenceContext
	private EntityManager entityManager;

	private User testUser;
	private String token;

	@BeforeEach
	public void setUp() {
		entityManager.clear();

		// 테스트용 사용자 생성
		testUser = User.builder()
			.name("테스트 유저")
			.authId("testUser")
			.email("testuser@example.com")
			.password("12345678")
			.build();
		userRepository.save(testUser);

		// JWT 토큰 생성
		this.token = jwtService.generateAccessTestToken(testUser.getAuthId(), testUser.getId());
	}

	@Test
	void getUserInfo() throws Exception {
		mockMvc.perform(get("/api/user")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("사용자 정보 조회 성공"))
			.andExpect(jsonPath("$.data.name").value(testUser.getName()));
	}

	@Test
	@WithMockUser
	void getUserInfo_Unauthorized() throws Exception {
		mockMvc.perform(get("/api/user")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("액세스 토큰이 없습니다"));
	}
}
