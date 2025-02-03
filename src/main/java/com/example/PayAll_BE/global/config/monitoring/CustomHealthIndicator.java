package com.example.PayAll_BE.global.config.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

// actuator.health
@Component
public class CustomHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		// 예: 외부 서비스 상태 확인 (가상의 로직 추가)
		boolean externalServiceUp = checkExternalService();

		if (externalServiceUp) {
			return Health.up()
				.withDetail("ExternalService", "Available")
				.withDetail("description", "외부 서비스가 정상적으로 동작 중입니다.")
				.build();
		} else {
			return Health.down()
				.withDetail("ExternalService", "Unavailable")
				.withDetail("description", "외부 서비스 연결에 문제가 발생했습니다.")
				.build();
		}
	}

	private boolean checkExternalService() {
		// 외부 서비스 확인 로직 (예: API 호출, DB 연결 확인 등)
		// 여기서는 가상 로직으로 항상 true 반환
		return true;
	}
}
