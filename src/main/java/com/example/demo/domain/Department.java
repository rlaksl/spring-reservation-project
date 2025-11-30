package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // 데이터베이스의 테이블과 매핑되는 JPA 엔티티임을 선언
@Getter @Setter // 모든 필드에 대한 Getter와 Setter를 자동 생성(Lombok)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 필수: 기본 생성자를 자동 생성(접근 레벨 Protected)
public class Department {

    @Id // PK 필드 선언
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPT_SEQ_GENERATOR") // ID 생성 전략: Oracle SEQUENCE 사용
    @SequenceGenerator(name = "DEPT_SEQ_GENERATOR", sequenceName = "DEPT_SEQ", allocationSize = 1) // 사용할 DB 시퀀스 이름과 설정을 매핑
    @Column(name = "dept_id") // DB 컬럼 이름을 지정
    private Long id; // 진료과 ID

    @Column(name = "dept_name", nullable = false, length = 50) // Not Null (필수), 길이 50 지정
    private String deptName; // 진료과 이름

    @Column(length = 500) // 길이 500 지정 (nullable = true가 기본값)
    private String description; // 진료과 설명

}
