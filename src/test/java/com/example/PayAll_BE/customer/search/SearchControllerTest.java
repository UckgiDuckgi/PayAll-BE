package com.example.PayAll_BE.customer.search;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
public class SearchControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@MockitoBean
	private SearchService searchService;

	@PersistenceContext
	private EntityManager entityManager;

	private User testUser;
	private String token;

	@Autowired
	private JwtService jwtService;

	@BeforeEach
	public void setUp() throws Exception {
		entityManager.clear();

		testUser = User.builder()
			.name("규호랑이")
			.authId("gyuhoTiger")
			.email("testuser1@example.com")
			.password("12345678")
			.permission(true)
			.build();
		userRepository.save(testUser);
		this.token = jwtService.generateAccessTestToken(testUser.getAuthId(), testUser.getId());

	}

	@Test
	void getSearchProductTest() throws Exception {
		String query = "물";

		List<SearchProductDto> productDtoList = Arrays.asList(SearchProductDto.builder()
			.pCode(4060647L)
			.productName("삼다수 2L (24개)")
			.productImage("https://img.danawa.com/prod_img/500000")
			.storeList(Arrays.asList(
				SearchProductDto.ShopInfoDto.builder()
					.shopName("11ST")
					.price(21890L)
					.shopUrl("https://www.11st.com/vp/products/7958974").build()
			))
			.build());

		when(searchService.getSearchProducts(query, 1, 5)).thenReturn(productDtoList);

		mockMvc.perform(get("/api/search")
				.param("query", query)
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("상품 검색 성공"))
			.andExpect(jsonPath("$.data[0].pcode").value(4060647L))
			.andExpect(jsonPath("$.data[0].productName").value("삼다수 2L (24개)"))
			.andExpect(jsonPath("$.data[0].storeList").isNotEmpty())
			.andDo(print());

		verify(searchService).getSearchProducts(query, 1, 5);
	}

	@Test
	void getSearchProductNoResultsTests() throws Exception {
		String query = "없음";

		List<SearchProductDto> emptyProductList = Arrays.asList();

		when(searchService.getSearchProducts(query, 1, 5)).thenReturn(emptyProductList);

		mockMvc.perform(get("/api/search")
				.param("query", query)
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("검색 결과가 없습니다."))
			.andDo(print());

		verify(searchService).getSearchProducts(query, 1, 5);

	}

}
