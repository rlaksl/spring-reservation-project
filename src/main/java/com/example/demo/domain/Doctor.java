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

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department; // 참조 대상 엔티티(객체 참조)

    @Column(nullable = false, length = 50)
    private String name; // 의사 이름

    public static Doctor createDoctor(String name, Department department) {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setDepartment(department);
        // 다른 필드 설정 (생략)
        return doctor;
    }

}

