package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileLog {
    private LocalDate yyyyMMdd;
    private Integer seq;
    private String userId;
    private String trait;
    private String ageGroup;
    private String summarize;
}
