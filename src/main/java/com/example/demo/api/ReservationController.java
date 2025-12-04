package com.example.demo.api;

import com.example.demo.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor // service 의존성 주입
@RequestMapping("/api/v1/reservations") // URL 경로 설정
public class ReservationController {

    private final ReservationService reservationService; // 서비스 계층 주입

    // POST: 진료 예약 생성 API
    // 실제로는 요청 본문(RequestBody)을 DTO로 받음(여기서는 간단하게 필드를 직접 받음)
    @PostMapping
    public ResponseEntity<Long> createReservation(
            @RequestParam("userId") Long userId,
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("reservationTime") String reservationTimeStr // 예약 시간 문자열로
    ) {
        try {
            // 문자열로 받은 시간을 LocalDateTime으로 변환 (실제로는 포맷팅 필요)
            LocalDateTime reservationTime = LocalDateTime.parse(reservationTimeStr);

            // service 계층의 비즈니스 로직 호출(예약 생성)
            Long reservationId = reservationService.createReservation(
                    userId, doctorId, reservationTime);

            // 성공
            return new ResponseEntity<>(reservationId, HttpStatus.CREATED);

        } catch (NoSuchElementException | IllegalStateException e) {
            // 실패
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 서버 오류
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
