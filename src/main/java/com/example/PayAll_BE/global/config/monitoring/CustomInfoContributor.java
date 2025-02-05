package com.example.PayAll_BE.global.config.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Map;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

// actuator.info
@Component
public class CustomInfoContributor implements InfoContributor {
	@Override
	public void contribute(Info.Builder builder) {

		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

		builder.withDetail("custom", "value")
			.withDetail("app", Map.of(
				"name", "PayAll",
				"description", "통합 장바구니, 최저가 검색 기능, 소비 분석 및 추천을 제공하여 절약에 도움을 준다.",
				"version", "1.0.0"
			))
			.withDetail("server", Map.of(
				"port", System.getProperty("server.port", "8080"),
				"environment", System.getProperty("spring.profiles.active", "default")
			))
			.withDetail("build", Map.of(
				"timestamp", System.getProperty("build.timestamp", "N/A"),
				"commit", System.getProperty("build.commit", "N/A")
			))
			.withDetail("java", Map.of(
				"version", System.getProperty("java.version"),
				"vendor", System.getProperty("java.vendor"),
				"runtime", System.getProperty("java.runtime.name")
			))
			.withDetail("os", Map.of(
				"name", osBean.getName(),
				"version", osBean.getVersion(),
				"arch", osBean.getArch(),
				"availableProcessors", osBean.getAvailableProcessors()
			));
	}
}
