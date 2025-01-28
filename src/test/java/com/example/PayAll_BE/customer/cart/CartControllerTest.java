package com.example.PayAll_BE.customer.cart;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import com.example.PayAll_BE.customer.cart.dto.CartRequestDto;
import com.example.PayAll_BE.customer.cart.dto.UpdateQuantityRequestDto;
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
public class CartControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private User testUser;
	private String token;
	private Cart testCart;

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

		testCart = Cart.builder()
			.user(testUser)
			.productId(4060647L)
			.productName("삼다수 2L (24개)")
			.productPrice(21890L)
			.quantity(1)
			.storeName("11ST")
			.link("https://www.11st.com/vp/products/7958974")
			.image("https://img.danawa.com/prod_img/500000")
			.prevPrice(22000L)
			.build();
		cartRepository.save(testCart);

	}

	@Test
	@DisplayName("장바구니 추가 테스트")
	void addCartTest() throws Exception {
		CartRequestDto requestDto = CartRequestDto.builder()
			.productId(1026291L)
			.productName("신라면 (5개)")
			.productImage("https://img.danawa.com/prod_img/500000")
			.shopName("Coupang")
			.shopUrl("https://www.coupang.com/vp/products/7958974")
			.price(3170L)
			.quantity(1)
			.prevPrice(3400L)
			.search(true)
			.build();

		String requestBody = objectMapper.writeValueAsString(requestDto);

		mockMvc.perform(post("/api/cart")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("장바구니 추가 성공"))
			.andExpect(jsonPath("$.data.cartId").isNotEmpty())
			.andDo(print());

	}

	@Test
	@DisplayName("장바구니 조회 테스트")
	void getCartsTest() throws Exception {
		mockMvc.perform(get("/api/cart")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("장바구니 내역 조회 성공"))
			.andExpect(jsonPath("$.data[0].productName").value("삼다수 2L (24개)"))
			.andExpect(jsonPath("$.data[0].productPrice").value(21890L))
			.andDo(print());

	}

	@Test
	@DisplayName("장바구니 수량 변경 테스트")
	void updateQuantityTest() throws Exception {
		Long cartId = testCart.getCartId();
		UpdateQuantityRequestDto requestDto = UpdateQuantityRequestDto.builder().quantity(4).build();

		String requestBody = objectMapper.writeValueAsString(requestDto);

		mockMvc.perform(patch("/api/cart/" + cartId)
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("상품 수량이 수정되었습니다."))
			.andDo(print());

	}

	@Test
	@DisplayName("장바구니 삭제 테스트")
	void deleteCartTest() throws Exception {
		Long cartId = testCart.getCartId();

		mockMvc.perform(delete("/api/cart/" + cartId)
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("상품이 장바구니에서 삭제되었습니다."))
			.andDo(print());
	}
}
