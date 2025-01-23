package com.example.PayAll_BE.global.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.PayAll_BE.global.auth.dto.RegisterRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;


	@Test
	void login() {
	}

	@Test
	@Order(1)
	void registerSuccess() throws Exception {
		// 정상적인 회원가입 요청
		RegisterRequestDto requestDto = RegisterRequestDto.builder()
			.name("규호랑이")
			.authId("gyuhoTiger")
			.password("password123")
			.phone("010-1234-5678")
			.address("서울시 강남구")
			.email("gyuho@example.com")
			.build();

		mockMvc.perform(post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))  // DTO를 JSON으로 변환하여 요청 본문에 담기
			.andExpect(status().isOk())  // HTTP 상태 코드가 200 OK인지 확인
			.andExpect(jsonPath("$.status").value("OK"))  // status가 "OK"인지 확인
			.andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));  // message가 올바른지 확인
	}

	@Test
	@Order(2)
	void registerFail_NoPassword() throws Exception {
		// 비밀번호가 없는 경우 회원가입 실패 테스트
		RegisterRequestDto requestDto = RegisterRequestDto.builder()
			.name("규호랑이")
			.authId("gyuhoTiger")
			.password("")  // 비밀번호를 빈 값으로 설정
			.phone("010-1234-5678")
			.address("서울시 강남구")
			.email("gyuho@example.com")
			.build();

		mockMvc.perform(post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))  // DTO를 JSON으로 변환하여 요청 본문에 담기
			.andExpect(status().isBadRequest())  // HTTP 상태 코드가 400 BadRequest인지 확인
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))  // status가 "BAD_REQUEST"인지 확인
			.andExpect(jsonPath("$.message").value("올바른 비밀번호를 입력해주세요."));  // message가 올바른지 확인
	}

	@Test
	void refreshToken() {
	}

	@Test
	void setPlatform() {
	}

	@Test
	void getPlatform() {
	}
}
