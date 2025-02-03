package com.example.PayAll_BE.global.config.monitoring;

import java.util.Map;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

// actuator.info
@Component
public class CustomInfoContributor implements InfoContributor {
	@Override
	public void contribute(Info.Builder builder) {
		builder.withDetail("custom", "value")
			.withDetail("app", Map.of(
				"name", "PayAll",
				"description", "통합 장바구니, 최저가 검색 기능, 소비 분석 및 추천을 제공하여 절약에 도움을 준다.",
				"version", "1.0.0"
			));
	}
}
