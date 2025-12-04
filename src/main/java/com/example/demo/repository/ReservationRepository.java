package com.example.demo.repository;

import com.example.demo.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDoctorIdAndReservationDatetimeAndStatusIn(
            Long doctorId,
            LocalDateTime reservationDatetime,
            List<String> statuses
    );
}
