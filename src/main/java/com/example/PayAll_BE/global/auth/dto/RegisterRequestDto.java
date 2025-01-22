package com.example.PayAll_BE.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
	private String name;
	private String authId;
	private String password;
	private String phone;
	private String address;
	private String email;
}
