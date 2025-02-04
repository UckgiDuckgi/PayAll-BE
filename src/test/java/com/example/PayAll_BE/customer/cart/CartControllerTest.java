package com.example.PayAll_BE.customer.cart;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.PayAll_BE.customer.cart.dto.CartRequestDto;
import com.example.PayAll_BE.customer.cart.dto.UpdateQuantityRequestDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductApiClient;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductDto;
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

	@MockitoBean
	private CrawlingProductApiClient crawlingProductApiClient;

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
	void addCartFromSearchTest() throws Exception {
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
	void addCartFromPaymentDetailTest() throws Exception {
		CrawlingProductDto productDto = CrawlingProductDto.builder()
			.pCode(1026291L)
			.productName("신라면 (5개)")
			.productImage("https://img.danawa.com/prod_img/500000")
			.shopName("Coupang")
			.shopUrl("https://www.coupang.com/vp/products/7958974")
			.price(3170L)
			.build();
		CartRequestDto requestDto = CartRequestDto.builder()
			.productId(1026291L)
			.quantity(1)
			.prevPrice(3400L)
			.search(false)
			.build();

		when(crawlingProductApiClient.fetchProduct(String.valueOf(requestDto.getProductId()))).thenReturn(productDto);

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

		verify(crawlingProductApiClient).fetchProduct(String.valueOf(requestDto.getProductId()));
	}

	@Test
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
	@WithMockUser
	void getCarts_Unauthorized() throws Exception {
		mockMvc.perform(get("/api/cart")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("액세스 토큰이 없습니다"));
	}

	@Test
	void updateQuantityTest() throws Exception {
		Long cartId = testCart.getCartId();
		int quantity = 4;
		UpdateQuantityRequestDto requestDto = UpdateQuantityRequestDto.builder().quantity(quantity).build();

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
	void updateQuantityBadRequestTest() throws Exception {
		Long cartId = testCart.getCartId();
		int quantity = -2;
		UpdateQuantityRequestDto requestDto = UpdateQuantityRequestDto.builder().quantity(quantity).build();

		String requestBody = objectMapper.writeValueAsString(requestDto);

		mockMvc.perform(patch("/api/cart/" + cartId)
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("수량은 1 이상이어야 합니다."))
			.andDo(print());

	}

	@Test
	void updateQuantityNotFoundTest() throws Exception {
		Long cartId = 9999L;
		int quantity = 2;
		UpdateQuantityRequestDto requestDto = UpdateQuantityRequestDto.builder().quantity(quantity).build();

		String requestBody = objectMapper.writeValueAsString(requestDto);

		mockMvc.perform(patch("/api/cart/" + cartId)
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("해당 장바구니 항목을 찾을 수 없습니다."))
			.andDo(print());

	}

	@Test
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

	@Test
	void deleteCartNotFoundTest() throws Exception {
		Long cartId = 9999L;

		mockMvc.perform(delete("/api/cart/" + cartId)
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("해당 장바구니 항목을 찾을 수 없습니다."))
			.andDo(print());

	}
}
