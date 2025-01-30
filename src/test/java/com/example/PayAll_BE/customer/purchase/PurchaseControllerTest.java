package com.example.PayAll_BE.customer.purchase;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.example.PayAll_BE.customer.cart.Cart;
import com.example.PayAll_BE.customer.cart.CartRepository;
import com.example.PayAll_BE.customer.payment.PaymentService;
import com.example.PayAll_BE.customer.search.SearchService;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.global.mydata.service.MydataService;
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
public class PurchaseControllerTest {
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
	private SearchService searchService;
	@MockitoBean
	private MydataService mydataService;
	@MockitoBean
	private PaymentService paymentService;

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
	void purchaseTest() throws Exception {
		PurchaseRequestDto.PurchaseProductDto productDto = PurchaseRequestDto.PurchaseProductDto.builder()
			.cartId(testCart.getCartId())
			.productId(4060647L)
			.productName("삼다수 2L (24개)")
			.productPrice(21890L)
			.quantity(1).build();
		PurchaseRequestDto requestDto = PurchaseRequestDto.builder()
			.purchaseList(List.of(productDto))
			.totalPrice(21890L)
			.totalDiscountPrice(2000L)
			.build();

		String requestBody = objectMapper.writeValueAsString(requestDto);

		doNothing().when(searchService).productInfoToRedis(requestDto.getPurchaseList().get(0).getProductId());
		when(mydataService.syncPurchaseData(token, requestDto)).thenReturn("123456789");
		doNothing().when(mydataService).syncMydataInfo(token);

		mockMvc.perform(post("/api/purchase")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("구매 성공"))
			.andDo(print());

		verify(searchService).productInfoToRedis(requestDto.getPurchaseList().get(0).getProductId());
		verify(mydataService).syncPurchaseData(token, requestDto);
		verify(mydataService).syncMydataInfo(token);

	}
}
