package com.example.demo.service;

import com.example.demo.config.HospitalProperties;
import com.example.demo.domain.Department;
import com.example.demo.domain.Doctor;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final ReservationRepository reservationRepository;
    private final HospitalProperties hospitalProperties;

    // 특정과에 소속된 의사 조회
    public List<Doctor> findDoctorsByDepartmentId (Long departmentId) {
        // 진료과 엔티티 찾기
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NoSuchElementException("해당 진료과를 찾을 수 없습니다"));

        // 의사 조회
        return doctorRepository.findByDepartment(department);
    }

    // 예약 단위 30분, 점심 13 - 14
    // 특정 의사 예약 조회
    public  List<LocalTime> getAvailableSlots(Long doctorId, LocalDate date) {
        // 의사 확인
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NoSuchElementException("해당 의사를 찾을 수 없습니다."));

        // 요일 확인
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 휴무일 체크
        if (dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek.name().equals(doctor.getDayOff())) {
            return List.of(); // 빈 리스트 반환 (예약 불가)
        }

        // 근무 시간 설정
        LocalTime startTime;
        LocalTime endTime;

        if (dayOfWeek == DayOfWeek.SATURDAY) { // 토요일
            startTime = LocalTime.parse(hospitalProperties.getSaturday().getStartTime());
            endTime = LocalTime.parse(hospitalProperties.getSaturday().getEndTime());
        } else {
            startTime = LocalTime.parse(hospitalProperties.getWeekday().getStartTime());
            endTime = LocalTime.parse(hospitalProperties.getWeekday().getEndTime());
        }

        // 점심 시간 설정
        LocalTime lunchStart = LocalTime.parse(hospitalProperties.getLunch().getStartTime());
        LocalTime lunchEnd = LocalTime.parse(hospitalProperties.getLunch().getEndTime());

        // 30분 단위
        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime currentTime = startTime;

        while (currentTime.plusMinutes(30).isBefore(endTime) || currentTime.plusMinutes(30).equals(endTime)) {
            // 점심 시간 제외
            if (dayOfWeek == DayOfWeek.SATURDAY || !isLunchTime(currentTime, lunchStart, lunchEnd)) {
                allSlots.add(currentTime);
            }
            currentTime = currentTime.plusMinutes(30);
        }

        // 이미 예약된 시간
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // 해당 의사의 해당 날짜 예약 조회
        // Repository에서 조회 → Stream으로 변환 → LocalTime만 추출 → List로 변환
        List<LocalTime> bookedTimes  = reservationRepository
                .findReservationsByDoctorAndDate(doctorId, startOfDay, endOfDay) // DB 조회
                .stream() // Stream 으로 변환
                .map(r -> r.getReservationDatetime().toLocalTime()) // LocalDateTime → LocalTime 변환
                .toList(); // List<LocalTime>으로 변환

        // 예약 가능한 시간 = 전체 슬롯 - 예약된 시간
        allSlots.removeAll(bookedTimes);

        return allSlots;
    }

    private boolean isLunchTime(LocalTime time, LocalTime start, LocalTime end) {
        return !time.isBefore(start) && time.isBefore(end);
    }
}
