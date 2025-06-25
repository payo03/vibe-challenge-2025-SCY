package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatResponse {
    private String message;
    private String userId; // 로그인하지 않은 경우 demo-user가 기본값
    private LocalDateTime timestamp;
    private boolean isUserMessage;
} 