// package com.example.PayAll_BE.customer.limit;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.time.LocalDateTime;
//
// import com.example.PayAll_BE.customer.account.Account;
// import com.example.PayAll_BE.customer.account.AccountRepository;
// import com.example.PayAll_BE.customer.enums.Category;
// import com.example.PayAll_BE.customer.enums.PaymentType;
// import com.example.PayAll_BE.customer.limit.dto.LimitRegisterRequestDto;
// import com.example.PayAll_BE.customer.payment.Payment;
// import com.example.PayAll_BE.customer.payment.PaymentRepository;
// import com.example.PayAll_BE.customer.statistics.Statistics;
// import com.example.PayAll_BE.customer.statistics.StatisticsRepository;
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
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
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
// @Transactional
// public class LimitControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
// 	@Autowired
// 	private JwtService jwtService;
// 	@Autowired
// 	private ObjectMapper objectMapper;
// 	@Autowired
// 	private UserRepository userRepository;
// 	@Autowired
// 	private LimitRepository limitRepository;
// 	@Autowired
// 	private PaymentRepository paymentRepository;
// 	@Autowired
// 	private StatisticsRepository statisticsRepository;
// 	@Autowired
// 	private AccountRepository accountRepository;
// 	@PersistenceContext
// 	private EntityManager entityManager;
//
// 	private String accessToken;
// 	private User testUser;
// 	private Account testAccount;
// 	private Limits thisMonthTestLimit;
// 	private Limits lastMonthTestLimit;
// 	private Payment thisMonthTestPayment;
// 	private Statistics thisMonthTestStatistics;
// 	private Statistics last1MonthTestStatistics;
// 	private Statistics last2MonthTestStatistics;
// 	private Statistics thisMonthDiscountTestStatistics;
//
// 	@BeforeEach
// 	public void setup() {
// 		limitRepository.deleteAll();
// 		paymentRepository.deleteAll();
// 		statisticsRepository.deleteAll();
// 		accountRepository.deleteAll();
// 		userRepository.deleteAll();
// 		entityManager.clear();
//
// 		testUser = User.builder()
// 			.name("TestUser")
// 			.authId("test99999")
// 			.password("password99999")
// 			.permission(true)
// 			.build();
// 		userRepository.save(testUser);
//
// 		testAccount = Account.builder()
// 			.user(testUser)
// 			.bankName("하나은행")
// 			.accountName("달달")
// 			.balance(1000000L)
// 			.accountNumber("111-111111-11-111")
// 			.build();
// 		accountRepository.save(testAccount);
//
// 		thisMonthTestLimit = Limits.builder()
// 			.user(testUser)
// 			.limitPrice(300000L)
// 			.limitDate(LocalDateTime.parse(("2025-01-24T08:00:00")))
// 			.build();
// 		lastMonthTestLimit = Limits.builder()
// 			.user(testUser)
// 			.limitPrice(350000L)
// 			.limitDate(LocalDateTime.parse(("2024-12-24T08:00:00")))
// 			.build();
// 		limitRepository.save(thisMonthTestLimit);
// 		limitRepository.save(lastMonthTestLimit);
//
// 		thisMonthTestPayment = Payment.builder()
// 			.account(testAccount)
// 			.paymentPlace("쿠팡")
// 			.price(25000L)
// 			.paymentType(PaymentType.ONLINE)
// 			.paymentTime(LocalDateTime.parse("2025-01-23T10:27:00"))
// 			.category(Category.SHOPPING)
// 			.build();
// 		paymentRepository.save(thisMonthTestPayment);
//
// 		thisMonthTestStatistics = Statistics.builder()
// 			.user(testUser)
// 			.category(Category.SHOPPING)
// 			.statisticsAmount(25000L)
// 			.statisticsDate(LocalDateTime.parse("2025-01-23T10:27:00"))
// 			.build();
// 		last1MonthTestStatistics = Statistics.builder()
// 			.user(testUser)
// 			.category(Category.SHOPPING)
// 			.statisticsAmount(40000L)
// 			.statisticsDate(LocalDateTime.parse("2024-12-23T10:27:00"))
// 			.build();
// 		last2MonthTestStatistics = Statistics.builder()
// 			.user(testUser)
// 			.category(Category.SHOPPING)
// 			.statisticsAmount(30000L)
// 			.statisticsDate(LocalDateTime.parse("2024-11-23T10:27:00"))
// 			.build();
// 		thisMonthDiscountTestStatistics = Statistics.builder()
// 			.user(testUser)
// 			.category(Category.DISCOUNT)
// 			.statisticsAmount(2500L)
// 			.statisticsDate(LocalDateTime.parse("2025-01-23T10:27:00"))
// 			.build();
// 		statisticsRepository.save(thisMonthTestStatistics);
// 		statisticsRepository.save(last1MonthTestStatistics);
// 		statisticsRepository.save(last2MonthTestStatistics);
// 		statisticsRepository.save(thisMonthDiscountTestStatistics);
//
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
// 		mockMvc.perform(get("/api/limit")
// 				.cookie(new Cookie("accessToken", accessToken))
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value(200))
// 			.andExpect(jsonPath("$.message").value("소비 목표 조회 성공"))
// 			.andExpect(jsonPath("$.data.limitPrice").value(300000L))
// 			.andExpect(jsonPath("$.data.spentAmount").value(25000L))
// 			.andExpect(jsonPath("$.data.savedAmount").value(2500L))
// 			.andExpect(jsonPath("$.data.averageSpent").value(32500L))
// 			.andExpect(jsonPath("$.data.lastMonthLimit").value(350000L));
// 	}
//
// 	@AfterAll
// 	public void afterAll(){
// 		limitRepository.deleteAll();
// 		paymentRepository.deleteAll();
// 		statisticsRepository.deleteAll();
// 		accountRepository.deleteAll();
// 		userRepository.deleteAll();
// 	}
// }
