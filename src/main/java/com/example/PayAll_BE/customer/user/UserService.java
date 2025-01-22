package com.example.PayAll_BE.customer.user;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.customer.user.dto.UserResponseDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final JwtService jwtService;

	public UserResponseDto getUserInfo(String token) {
		String authId = jwtService.extractAuthId(token);

		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

		return UserResponseDto.builder()
			.name(user.getName())
			.authId(user.getAuthId())
			.phone(user.getPhone())
			.address(user.getAddress())
			.build();
	}
}
