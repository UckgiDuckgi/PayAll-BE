package com.example.PayAll_BE.service;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.UserResponseDto;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public UserResponseDto getUserInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

		return UserResponseDto.builder()
			.name(user.getName())
			.authId(user.getAuthId())
			.phone(user.getPhone())
			.address(user.getAddress())
			.build();
	}
}
