package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileLog {
    private String userId;
    private String trait;
    private String lastAnswer;
    private String ageGroup;
} 