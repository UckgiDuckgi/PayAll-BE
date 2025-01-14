package com.example.PayAll_BE.service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.RegisterRequestDto;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.BadRequestException;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;

	public ApiResult register(RegisterRequestDto request) {
		try {
			// Validate simple password
			if (request.getPassword() == null || request.getPassword().length() < 6) {
				throw new BadRequestException("올바른 비밀번호를 입력해주세요.");
			}

			// 전화번호 형식 통일
			String formattedPhone = request.getPhone().replaceAll("-", "");

			Optional<User> existingUser = userRepository.findAll().stream()
				.filter(user ->
					user.getName().equals(request.getName()) &&
						user.getPhone().replaceAll("-", "").equals(formattedPhone)
				)
				.findFirst();

			// 기존 사용자가 있는 경우
			if (existingUser.isPresent()) {
				User user = existingUser.get();
				throw new BadRequestException("이미 가입된 사용자 입니다.");
			}

			// 새로운 사용자 생성
			User newUser = User.builder()
				.authId(request.getAuthId())
				.name(request.getName())
				.phone(formattedPhone)
				.address(request.getAddress())
				.password(request.getPassword())
				.build();

			userRepository.save(newUser);
			return new ApiResult(200, "NEW", "회원가입 성공", null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("회원가입 실패: " + e.getMessage());
		}
	}
}
