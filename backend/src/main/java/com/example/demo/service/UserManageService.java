package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserManageService {
    
    private final Map<String, LocalDateTime> lastActivityMap = new ConcurrentHashMap<>();

    @Value("${default.user}")
    private String defaultUser;

    public void updateUserActivity(String userId, LocalDateTime time) {
        if (!defaultUser.equals(userId)) lastActivityMap.put(userId, time);
    }

    public Map<String, LocalDateTime> getAllUsers() {
        return lastActivityMap;
    }

    public void finishUser(String userId) {

        if(!defaultUser.equals(userId)) {
            this.removeUser(userId);
            this.summarize(userId);
        }
    }

    // 내부함수
    private void removeUser(String userId) {
        lastActivityMap.remove(userId);
    }

    private void summarize(String userId) {

    }
}
