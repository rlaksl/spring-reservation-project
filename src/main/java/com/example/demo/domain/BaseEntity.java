package com.example.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 이 클래스는 테이블로 매핑되지 않고, 상속 관계에서 필드만 공유
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간 자동 기록 활성화
public class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", updatable = false) // 생성 시에만 값을 설정하고 이후 변경 불가능
    private LocalDateTime createdAt;

}
