package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatResponse {
    private String message;
    private String sessionId;
    private LocalDateTime timestamp;
    private boolean isUserMessage;
} 