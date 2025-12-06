package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "hospital.schedule")
public class HospitalProperties {

    private TimeSlot weekday;
    private TimeSlot saturday;
    private TimeSlot lunch;

    // 내부 클래스: 시작,종료 시간-
    @Getter @Setter
    public  static class TimeSlot {
        private String startTime; // yml의 start-time과 자동 매핑
        private String endTime;
    }
}
