package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Doctor extends BaseEntity{ // BaseEntity 상속(created_at 필드 자동 상속)

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCTOR_SEQ_GENERATOR")
    @SequenceGenerator(name = "DOCTOR_SEQ_GENERATOR", sequenceName = "DOCTOR_SEQ", allocationSize = 1)
    @Column(name = "doctor_id")
    private Long id; // 의사 ID

    // FK - 소속 진료과
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department; // 참조 대상 엔티티(객체 참조)

    @Column(nullable = false, length = 50)
    private String name; // 의사 이름

    // 개인 휴무일(추가함)
    @Column(name = "day_off", length = 10)
    private String dayOff;

    // dayOff 추가
    public static Doctor createDoctor(String name, Department department, String dayOff) {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setDepartment(department);
        doctor.setDayOff(dayOff);
        return doctor;
    }

}

