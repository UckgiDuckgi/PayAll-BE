// package com.example.PayAll_BE.customer.user;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.MethodOrderer;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.junit.jupiter.api.TestMethodOrder;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.example.PayAll_BE.customer.user.User;
// import com.example.PayAll_BE.customer.user.UserRepository;
// import com.example.PayAll_BE.global.auth.service.JwtService;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;
// import jakarta.servlet.http.Cookie;
// import jakarta.transaction.Transactional;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @Transactional
// class UserControllerTest {
//
// 	@Autowired
// 	private UserRepository userRepository;
//
// 	@PersistenceContext
// 	private EntityManager entityManager;
//
// 	@Autowired
// 	private MockMvc mockMvc;
// 	@Autowired
// 	private JwtService jwtService;
//
// 	private User testUser1;
// 	private String token;
//
// 	@BeforeEach
// 	public void setUp() {
// 		userRepository.deleteAll();
// 		entityManager.clear();
// 		// 테스트용 사용자 생성
// 		testUser1 = User.builder()
// 			.name("규호랑이")
// 			.authId("gyuhoTiger")
// 			.email("testuser1@example.com")
// 			.password("12345678")
// 			.build();
// 		userRepository.save(testUser1);
//
// 		this.token = jwtService.generateAccessTestToken(testUser1.getAuthId(), testUser1.getId());
// 	}
//
// 	@Test
// 	void getUserInfo() throws Exception {
// 		mockMvc.perform(get("/api/user")
// 				.cookie(new Cookie("accessToken", token))  // 쿠키로 accessToken 전달
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk())  // HTTP 상태 코드가 200 OK인지 확인
// 			.andExpect(jsonPath("$.status").value("OK"))  // 응답 JSON의 status 필드가 "OK"인지 확인
// 			.andExpect(jsonPath("$.message").value("사용자 계좌 목록 조회 성공"))  // 응답 메시지 확인
// 			.andExpect(jsonPath("$.data.name").value(testUser1.getName()))
// 			.andExpect(jsonPath("$.data.authId").value(testUser1.getAuthId()));
// 	}
//
//
// 	@AfterAll
// 	public void AfterAll() {
// 		userRepository.deleteAll();
//
// 	}
// }
