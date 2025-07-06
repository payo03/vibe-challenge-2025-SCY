package com.example.demo.service;

import com.example.demo.dto.ParsedResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResponseParserServiceTest {
    ResponseParserService parserService = new ResponseParserService();

    @Test
    void testParseGeminiResponse_basic() {
        String aiText = "안녕하세요!";
        ParsedResponse res = parserService.parseGeminiResponse(aiText);
        assertEquals("안녕하세요!", res.getUserMessage());
        assertTrue(res.getWeatherInfo().isEmpty());
    }

    @Test
    void testParseGeminiResponse_withWeatherJson() {
        String aiText = "{\"destination\":\"서울\",\"date\":\"2024-07-01\"} 날씨 정보입니다.";
        ParsedResponse res = parserService.parseGeminiResponse(aiText);
        assertEquals("날씨 정보입니다.", res.getUserMessage());
        assertEquals("서울", res.getWeatherInfo().get("destination"));
        assertEquals("2024-07-01", res.getWeatherInfo().get("date"));
    }
} 