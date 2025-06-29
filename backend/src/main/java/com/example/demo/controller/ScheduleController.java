package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.service.CommonService;

@Component
public class ScheduleController {

    @Autowired
    CommonService commonService;
    
    @Scheduled(fixedRate = 120000)
    public void checkInactiveUsers() {
        LocalDateTime now = LocalDateTime.now();

        Map<String, Map<String, Object>> userInfoMap = commonService.getAllUsers();
        for (String userId : userInfoMap.keySet()) {
            Map<String, Object> userTimeMap = userInfoMap.get(userId);
            LocalDateTime lastTime = userTimeMap.containsKey("lastActiveTime") 
                ? (LocalDateTime) userTimeMap.get("lastActiveTime") 
                : now;

            // 변수값 정리, 요약본 저장
            if (lastTime.isBefore(now.minusMinutes(2))) commonService.finishUser(userId);
        }
    }
    //
}
