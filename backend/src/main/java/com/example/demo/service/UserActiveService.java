package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class UserActiveService {

    private final Map<String, LocalDateTime> lastActivityMap = new ConcurrentHashMap<>();

    public void updateUserActivity(String userId, LocalDateTime time) {
        System.out.println("HERE TO");
        lastActivityMap.put(userId, time);
    }

    public LocalDateTime getLastActivity(String userId) {
        return lastActivityMap.get(userId);
    }

    public Map<String, LocalDateTime> getAllUsers() {
        return lastActivityMap;
    }

    public void removeUser(String userId) {
        System.out.println("HERE TO REMOVE");
        lastActivityMap.remove(userId);
    }
}
