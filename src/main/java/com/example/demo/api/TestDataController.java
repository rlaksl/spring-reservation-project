package com.example.demo.api;

import com.example.demo.domain.Department;
import com.example.demo.domain.Doctor;
import com.example.demo.domain.User;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TestDataController {

    private final InitService initService;

    // 서버 시작 시 자동 실행
    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @GetMapping("/api/v1/test/init")
    public String initTestData() {
        return "데이터 초기화 로직이 실행되었습니다. (콘솔 로그를 확인하세요)";
    }

    @Service
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final UserRepository userRepository;
        private final DepartmentRepository departmentRepository;
        private final DoctorRepository doctorRepository;

        public void dbInit() {
            // 데이터가 이미 있으면 초기화 x
            if (userRepository.count() > 0) {
                System.out.println("[SKIP] 데이터가 이미 존재하여 초기화를 건너뜁니다.");
                return;
            }

            System.out.println("[START] 테스트 데이터 생성을 시작합니다.");

            // 1. 진료과 생성
            Department dept1 = Department.createDepartment("내과", "일반 진료 및 내과 전문");
            Department dept2 = Department.createDepartment("이비인후과", "이비인후과");
            Department dept3 = Department.createDepartment("소아과", "소아과");

            departmentRepository.save(dept1);
            departmentRepository.save(dept2);
            departmentRepository.save(dept3);

            // 2. 의사 생성
            Doctor doctor1 = Doctor.createDoctor("김철수 의사", dept1, "MONDAY"); // 내과
            Doctor doctor2 = Doctor.createDoctor("홍길동 의사", dept1, "TUESDAY");
            Doctor doctor3 = Doctor.createDoctor("이나영 의사", dept2, "WEDNESDAY"); // 이비인후과
            Doctor doctor4 = Doctor.createDoctor("김환희 의사", dept2, "THURSDAY");
            Doctor doctor5 = Doctor.createDoctor("박지민 의사", dept3, "FRIDAY"); // 소아과
            Doctor doctor6 = Doctor.createDoctor("이윤서 의사", dept3, "MONDAY");

            doctorRepository.save(doctor1);
            doctorRepository.save(doctor2);
            doctorRepository.save(doctor3);
            doctorRepository.save(doctor4);
            doctorRepository.save(doctor5);
            doctorRepository.save(doctor6);

            // 3. 환자 생성
            User user = User.createUser(
                    "이영희",
                    "yh.lee@test.com",
                    "hashed_password",
                    "01012345678",
                    LocalDate.of(1990, 5, 15)
            );
            userRepository.save(user);

            // 테이블 수정 후 스케줄 생성 로직 삭제했음

            System.out.println("[END] 테스트 데이터 생성이 완료되었습니다.");
        }
    }
}