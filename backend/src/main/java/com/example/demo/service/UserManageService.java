package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class UserManageService {

    private final Map<String, LocalDateTime> lastActivityMap = new ConcurrentHashMap<>();

    public void updateUserActivity(String userId, LocalDateTime time) {
        lastActivityMap.put(userId, time);
    }

    public LocalDateTime getLastActivity(String userId) {
        return lastActivityMap.get(userId);
    }

    public Map<String, LocalDateTime> getAllUsers() {
        return lastActivityMap;
    }

    public void removeUser(String userId) {
        lastActivityMap.remove(userId);
    }

    public void summarize(String userId) {
        
    }

    public void finishUser(String userId) {
        this.removeUser(userId);
        this.summarize(userId);
    }
}
