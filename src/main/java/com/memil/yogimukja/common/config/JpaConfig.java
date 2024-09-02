package com.memil.yogimukja.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정 클래스
 */
@Configuration
@EnableJpaAuditing // Auditing 활성화 -> createdDate, updatedDate 자동 관리
public class JpaConfig {
}
