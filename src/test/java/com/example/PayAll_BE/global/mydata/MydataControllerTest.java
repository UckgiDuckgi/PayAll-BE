package com.example.PayAll_BE.global.mydata;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.global.mydata.controller.MydataController;
import com.example.PayAll_BE.global.mydata.dto.AccountBasicInfoDto;
import com.example.PayAll_BE.global.mydata.dto.AccountRequestDto;
import com.example.PayAll_BE.global.mydata.dto.AccountResponseDto;
import com.example.PayAll_BE.global.mydata.dto.AccountTransactionDto;
import com.example.PayAll_BE.global.mydata.dto.TransactionRequestDto;
import com.example.PayAll_BE.global.mydata.dto.TransactionResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class MydataControllerTest {

	@Mock
	private RestTemplate restTemplate;  // 외부 API 호출을 Mock 처리

	@InjectMocks
	private MydataController mydataController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(mydataController).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	void getAccountBasicInfo_Success() throws Exception {
		AccountRequestDto requestDto = AccountRequestDto.builder()
			.orgCode("BANK123")
			.accountNum("123-456-789")
			.searchTimestamp("20240204")
			.build();

		AccountResponseDto responseDto = AccountResponseDto.builder()
			.rspCode("0000")
			.rspMsg("정상처리")
			.baseDate("2024-02-04")
			.basicCnt(1)
			.basicList(List.of(
				AccountBasicInfoDto.builder()
					.accountNum("123-456-789")
					.baseDate("2025-02-04")
					.currencyCode("30")
					.withholdingsAmt(BigDecimal.valueOf(1000000L))
					.creditLoanAmt(BigDecimal.valueOf(1000000L))
					.mortgageAmt(BigDecimal.valueOf(1000000L))
					.availBalance(BigDecimal.valueOf(1000000L))
					.build()
			))
			.build();
		String requestJson = objectMapper.writeValueAsString(requestDto);
		String responseJson = objectMapper.writeValueAsString(responseDto);

		when(restTemplate.exchange(
			anyString(),
			eq(HttpMethod.POST),
			any(HttpEntity.class),
			eq(AccountResponseDto.class)
		)).thenReturn(new ResponseEntity<>(responseDto, HttpStatus.OK));

		mockMvc.perform(post("/api/mydata/basic")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson));

		verify(restTemplate, times(1)).exchange(
			anyString(),
			eq(HttpMethod.POST),
			any(HttpEntity.class),
			eq(AccountResponseDto.class)
		);
	}

	@Test
	void getAccountTransactions_Success() throws Exception {
		TransactionRequestDto requestDto = TransactionRequestDto.builder()
			.orgCode("BANK123")
			.accountNum("123-456-789")
			.fromDate(Timestamp.valueOf("2025-01-02 10:50:02"))
			.fromDate(Timestamp.valueOf("2024-12-25 10:50:02"))
			.nextPage("22")
			.limit(2)
			.build();

		TransactionResponseDto responseDto = TransactionResponseDto.builder()
			.rspCode("0000")
			.rspMsg("정상처리")
			.nextPage("23")
			.transCnt(1)
			.transList(List.of(
				AccountTransactionDto.builder()
					.prodName("삼성전자 주식")             // 상품명
					.prodCode("005930")                 // 상품 코드
					.transDtime(new Timestamp(System.currentTimeMillis())) // 현재 시간
					.transNo("TRX1234567890")           // 거래 번호
					.transType("BUY")                   // 거래 유형 (예: 매수)
					.transTypeDetail("Stock Purchase")  // 거래 유형 상세
					.transNum(BigDecimal.valueOf(10))   // 거래 수량
					.transUnit("주")                     // 거래 단위 (주식)
					.baseAmt(BigDecimal.valueOf(85000)) // 거래단가
					.transAmt(BigDecimal.valueOf(850000)) // 거래금액
					.settleAmt(BigDecimal.valueOf(850000)) // 정산금액
					.balanceAmt(BigDecimal.valueOf(5000000)) // 거래 후 잔액
					.currencyCode("KRW")                // 통화 코드 (한국 원)
					.transMemo("주식 매수 거래")         // 적요 (메모)
					.exCode("KRX")                      // 거래소 코드 (한국거래소)
					.build()
			))
			.build();
		String requestJson = objectMapper.writeValueAsString(requestDto);
		String responseJson = objectMapper.writeValueAsString(responseDto);

		when(restTemplate.exchange(
			anyString(),
			eq(HttpMethod.POST),
			any(HttpEntity.class),
			eq(TransactionResponseDto.class)
		)).thenReturn(new ResponseEntity<>(responseDto, HttpStatus.OK));

		mockMvc.perform(post("/api/mydata/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson));

		verify(restTemplate, times(1)).exchange(
			anyString(),
			eq(HttpMethod.POST),
			any(HttpEntity.class),
			eq(TransactionResponseDto.class)
		);
	}
}
