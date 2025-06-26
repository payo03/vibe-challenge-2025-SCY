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
    
    @Scheduled(fixedRate = 120000)
    public void checkInactiveUsers() {
        LocalDateTime now = LocalDateTime.now();

        Map<String, LocalDateTime> userInfoMap = manageService.getAllUsers();
        for (String userId : userInfoMap.keySet()) {
            LocalDateTime lastTime = userInfoMap.get(userId);

            // 변수값 정리, 요약본 저장
            if (lastTime.isBefore(now.minusMinutes(1))) manageService.finishUser(userId);
        }
    }
    //
}
