// package com.example.PayAll_BE.customer.limit;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.time.LocalDate;
//
// import com.example.PayAll_BE.customer.limit.dto.LimitRegisterRequestDto;
// import com.example.PayAll_BE.customer.limit.dto.LimitResponseDto;
// import com.example.PayAll_BE.customer.user.User;
// import com.example.PayAll_BE.customer.user.UserRepository;
// import com.example.PayAll_BE.global.api.ApiResult;
// import com.example.PayAll_BE.global.auth.service.JwtService;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;
// import jakarta.servlet.http.Cookie;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// public class LimitControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
// 	@Autowired
// 	private JwtService jwtService;
// 	@Autowired
// 	private ObjectMapper objectMapper;
// 	@Autowired
// 	private LimitService limitService;
// 	@Autowired
// 	private UserRepository userRepository;
// 	@PersistenceContext
// 	private EntityManager entityManager;
//
// 	private String accessToken;
// 	private User testUser;
//
// 	@BeforeEach
// 	public void setup() {
// 		entityManager.clear();
// 		testUser = User.builder()
// 			.name("TestUser")
// 			.authId("test99999")
// 			.password("password99999")
// 			.permission(true)
// 			.build();
// 		userRepository.save(testUser);
// 		this.accessToken = jwtService.generateAccessTestToken(testUser.getAuthId(), testUser.getId());
// 	}
//
// 	@Test
// 	void registerLimit() throws Exception {
// 		LimitRegisterRequestDto requestDto = LimitRegisterRequestDto.builder()
// 			.limitPrice(50000L)
// 			.build();
//
// 		mockMvc.perform(post("/api/limit")
// 				.cookie(new Cookie("accessToken", accessToken))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(requestDto)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value(200))
// 			.andExpect(jsonPath("$.message").value("소비 목표 등록 성공"));
// 	}
//
// 	@Test
// 	void getLimit() throws Exception {
// 		//account, payment, statistics 데이터 만들어넣고 테스트 해야함, 일단 수정사항 반영하러 가보겠음.
//
// 		mockMvc.perform(get("/api/limit")
// 				.cookie(new Cookie("accessToken", accessToken))
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value(200))
// 			.andExpect(jsonPath("$.message").value("소비 목표 조회 성공"))
// 			.andExpect(jsonPath("$.data.limitPrice").value(50000L))
// 			.andExpect(jsonPath("$.data.spentAmount").value(20000L))
// 			.andExpect(jsonPath("$.data.savedAmount").value(30000L))
// 			.andExpect(jsonPath("$.data.averageSpent").value(25000L))
// 			.andExpect(jsonPath("$.data.startDate").value("2025-01-01"))
// 			.andExpect(jsonPath("$.data.endDate").value("2025-01-31"));
// 	}
// }
