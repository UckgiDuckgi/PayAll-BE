package com.example.PayAll_BE.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformRequestDto {
	private String coupangId;
	private String coupangPassword;
	private String elevenstId;
	private String elevenstPassword;
	private String naverId;
	private String naverPassword;
}
