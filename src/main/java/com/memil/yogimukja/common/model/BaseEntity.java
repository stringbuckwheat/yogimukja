package com.memil.yogimukja.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 전체 엔티티에서 공통으로 사용할 기본 엔티티 클래스
 * 엔티티 생성/수정 시 일자 관리
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate; // 생성 일자

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedDate; // 마지막 수정 일자
}
