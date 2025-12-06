package com.example.demo.repository;

import com.example.demo.domain.Department;
import com.example.demo.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // 특정과 의사 목록 조회
     List<Doctor> findByDepartment(Department department);

}
