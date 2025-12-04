package com.example.demo.api;

import lombok.Data;

@Data
public class ReservationRequestDto {
    private Long userId;
    private Long doctorId;
    private String reservationTimeStr;
}
