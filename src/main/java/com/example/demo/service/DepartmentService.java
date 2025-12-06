package com.example.demo.service;

import com.example.demo.domain.Department;
import com.example.demo.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    // 모든과 조회
    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
    }
}
