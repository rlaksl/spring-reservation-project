package com.example.demo.api;

import com.example.demo.domain.Department;
import com.example.demo.domain.Doctor;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final DoctorService doctorService;

    // GET: 모든 진료과 목록
    @GetMapping("/departments")
    public List<Department> listDepartments() {
        return departmentService.findAllDepartments();
    }

    // GET: 특정과 소속 의사 목록
    @GetMapping("/departments/{deptId}/doctors")
    public List<Doctor> listDoctorsByDepartment(@PathVariable("deptId") Long deptId) {
        return doctorService.findDoctorsByDepartmentId(deptId);
    }

    @GetMapping("/doctors/{doctorId}/availability")
    public List<LocalTime> getDoctorAvailability(
            @PathVariable("doctorId") Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // DoctorService의 getAvailableSlots 메서드 호출 (예약 가능 시간 계산)
        return doctorService.getAvailableSlots(doctorId, date);
    }
}
