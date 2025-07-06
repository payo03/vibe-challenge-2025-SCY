package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedResponse {
    private String userMessage;                    // 사용자에게 보여줄 메시지
    private Map<String, Object> weatherInfo;       // 날씨 요청 정보
}