package com.example.PayAll_BE.global.auth.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformResponseDto {
	private List<PlatformInfo> platformInfos;

	@Data
	@Builder
	public static class PlatformInfo {
		private String platformName;
		private String id;
	}

}
