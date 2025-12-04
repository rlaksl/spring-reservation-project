package com.example.demo.service;

import com.example.demo.domain.Doctor;
import com.example.demo.domain.Reservation;
import com.example.demo.domain.User;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.DoctorScheduleRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    // 필요한 Repository 의존성 주입
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    // 진료 예약 생성
    @Transactional // 데이터 변경 트랜잭션 적용
    public Long createReservation(Long userId, Long doctorId, LocalDateTime reservationTime) {

        // 엔티티 조회 및 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("환자를 찾을 수 없습니다."));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NoSuchElementException("의사를 찾을 수 없습니다."));

        // [비즈니스 로직] 예약 가능 시간 확인 및 중복 검증
        validateDuplicateReservation(doctorId, reservationTime);

        // 예약 엔티티 생성 및 저장
        Reservation reservation = Reservation.createReservation(user, doctor, reservationTime);
        reservation.setUser(user);
        reservation.setDoctor(doctor);
        reservation.setReservationDatetime(reservationTime);
        reservation.setStatus("PENDING"); // 초기 상태는 PENDING

        reservationRepository.save(reservation);
        return reservation.getId();

    }

    // 중복 예약 검증 메서드(해당 시간에 확정 or 대기 중인 예약이 있는지)
    private void validateDuplicateReservation(Long doctorId, LocalDateTime reservationTime) {
        // 이미 예약이 잡혀있는 상태 목록
        List<String> occupiedStatuses = List.of("CONFIRMED", "PENDING");

        // Repository에 정의된 쿼리 메서드를 사용하여 충돌 예약 리스트 조회
        List<Reservation> duplicateReservations = reservationRepository
                .findByDoctorIdAndReservationDatetimeAndStatusIn(
                        doctorId,
                        reservationTime,
                        occupiedStatuses
                );

        if (!duplicateReservations.isEmpty()) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }

    }

}