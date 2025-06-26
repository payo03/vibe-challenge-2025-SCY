package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.service.UserManageService;

@Component
public class ScheduleController {

    @Autowired
    UserManageService manageService;
    
    @Scheduled(fixedRate = 60000)
    public void checkInactiveUsers() {
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, LocalDateTime> entry : manageService.getAllUsers().entrySet()) {
            String userId = entry.getKey();
            LocalDateTime lastTime = entry.getValue();

            // 변수값 정리, 요약본 저장
            if (lastTime.isBefore(now.minusMinutes(1))) manageService.finishUser(userId);
        }
    }
}
