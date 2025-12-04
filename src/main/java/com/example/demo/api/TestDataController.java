package com.example.demo.api;

import com.example.demo.domain.Department;
import com.example.demo.domain.Doctor;
import com.example.demo.domain.User;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.UserRepository;
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

    @GetMapping("/api/v1/test/init")
    public String initTestData() {
        initService.dbInit();
        return "테스트 데이터 초기화 완료";
    }

    @Service
    @Transactional // DB 쓰기 작업이므로 트랜잭션 필요
    static class InitService {
        private final UserRepository userRepository;
        private final DepartmentRepository departmentRepository;
        private final DoctorRepository doctorRepository;

        public InitService(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           DoctorRepository doctorRepository) {
            this.userRepository = userRepository;
            this.departmentRepository = departmentRepository;
            this.doctorRepository = doctorRepository;
        }

        public void dbInit() {
            // 1. 진료과 생성
            // new Department() 대신 Department.createDepartment() 호출
            Department dept = Department.createDepartment("내과", "일반 진료 및 내과 전문");
            departmentRepository.save(dept);

            // 2. 의사 생성 (내과 소속)
            Doctor doctor = Doctor.createDoctor("김철수 의사", dept); // 수정 완료
            doctorRepository.save(doctor);

            // 3. 환자 생성
            User user = User.createUser(
                    "이영희",
                    "yh.lee@test.com",
                    "hashed_password",
                    "01012345678",
                    LocalDate.of(1990, 5, 15)
            ); // 수정 완료
            userRepository.save(user);
        }
    }
}