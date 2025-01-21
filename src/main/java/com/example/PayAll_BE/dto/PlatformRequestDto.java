package com.example.PayAll_BE.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformRequestDto {
	private String platformName;
	private String id;
	private String password;
}
