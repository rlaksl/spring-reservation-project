package com.example.demo.repository;

import com.example.demo.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDoctorIdAndReservationDatetimeAndStatusIn(
            Long doctorId,
            LocalDateTime reservationDatetime,
            List<String> statuses
    );

    @Query("SELECT r " +
            "FROM Reservation r " +
            "WHERE r.doctor.id = :doctorId " +
            "AND r.reservationDatetime >= :startOfDay " +
            "AND r.reservationDatetime < :endOfDay " +
            "AND r.status IN ('CONFIRMED', 'PENDING')")
    List<Reservation> findReservationsByDoctorAndDate(
            @Param("doctorId") Long doctorId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);
}
