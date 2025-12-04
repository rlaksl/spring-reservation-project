package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter // 이 @Setter 덕분에 Service에서 setReservationDatetime 호출 가능
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESERVATION_SEQ_GENERATOR")
    @SequenceGenerator(name = "RESERVATION_SEQ_GENERATOR", sequenceName = "RESERVATION_SEQ", allocationSize = 1)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "reservation_datetime", nullable = false)
    private LocalDateTime reservationDatetime;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // 예약 상태

    // updated_at 필드는 BaseEntity에서 처리하는 것이 일반적
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 모든 필수 필드를 받아 객체를 생성하는 메서드
    public static Reservation createReservation(User user, Doctor doctor, LocalDateTime reservationTime) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setDoctor(doctor);
        reservation.setReservationDatetime(reservationTime);
        reservation.setStatus("PENDING");
        return reservation;

    }
}