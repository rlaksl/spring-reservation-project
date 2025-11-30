package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DOCTOR_SCHEDULE")
// @Table을 생략하는 경우 (기본값) - 엔티티 클래스 이름 = DB 테이블 이름 (대소문자만 다르고 완전히 동일)
// @Table을 필수로 쓰는 경우 - 엔티티 클래스 이름과 테이블 이름이 다를 때/ 예약어 충돌을 피하기 위해
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SCHEDULE_SEQ_GENERATOR")
    @SequenceGenerator(name = "SCHEDULE_SEQ_GENERATOR", sequenceName = "SCHEDULE_SEQ", allocationSize = 1)
    @Column
    private Long id;

    // FK
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "day_of_week", nullable = false, length = 10) // 요일 정보
    private String dayOfWeek;

    @Column(name = "start_time", nullable = false, length = 5) // 시작 시간 문자열 (HH:MM)
    private String startTime;

    @Column(name = "end_time", nullable = false, length = 5)
    private String endTime;

    @Column(name = "is_available", length = 1) // 진료 가능 여부 (휴무일 설정 시 'N')
    private String isAvailable;



}
