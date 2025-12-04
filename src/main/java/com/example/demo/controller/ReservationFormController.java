package com.example.demo.controller;

import com.example.demo.api.ReservationRequestDto;
import com.example.demo.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller; // @Controller (화면용)
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ReservationFormController {
    private final ReservationService reservationService;

    // 예약 화면: GET/reservation/new
    @GetMapping("/reservations/new")
    public String createForm(Model model) {
        // HTML 파일에 데이터를 넘겨주기 위해 빈 DTO 객체를 모달에 담기
        model.addAttribute("reservaionForm", new ReservationRequestDto());

        return "reservations/createReservationForm";
    }

    // 예약 처리
    @PostMapping("/reservations/new")
    public String create(@ModelAttribute("reservationForm") ReservationRequestDto form) {
        try {
            LocalDateTime reservationTime = LocalDateTime.parse(form.getReservationTimeStr());
            reservationService.createReservation(form.getUserId(), form.getDoctorId(), reservationTime);
            return "redirect:/"; // 성공 시 리다이렉트
        } catch (Exception e) {
            return "reservations/createReservationForm"; // 실패 시 폼 화면으로
        }
    }
}
