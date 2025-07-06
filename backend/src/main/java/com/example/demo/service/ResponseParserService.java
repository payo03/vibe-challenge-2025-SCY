package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ParsedResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ResponseParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseParserService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 날씨 요청 JSON 패턴들
    private static final Pattern WEATHER_JSON_PATTERNS = Pattern.compile(
            "\\{\\s*\"destination\"\\s*:\\s*\"[^\"]+\"\\s*,\\s*\"date\"\\s*:\\s*\"[^\"]+\"\\s*\\}", 
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    /**
     * Gemini AI 응답을 파싱하여 사용자 메시지와 날씨 요청을 분리
     */
    public ParsedResponse parseGeminiResponse(String aiText) {
        String modelMessage = aiText;
        Map<String, Object> weatherInfo = new HashMap<>();

        try {
            logger.info("=== Gemini 응답 파싱 시작 ===");
            logger.info("원본 응답: {}", aiText);
            
            // 1. 날씨 요청 JSON이 포함되어 있는지 확인
            weatherInfo = extractWeatherInfo(aiText);
            
            // 2. JSON을 제외한 사용자 메시지 추출
            modelMessage = extractUserMessage(aiText);
            
            // 3. 결과 로깅
            logger.info("날씨 요청 감지: {}", weatherInfo);
            logger.info("사용자 메시지: {}", modelMessage);
        } catch (Exception e) {
            logger.error("Gemini 응답 파싱 중 오류 발생: ", e);
        }
        
        return new ParsedResponse(modelMessage, weatherInfo);
    }
    
    /**
     * AI 응답에서 날씨 요청 JSON 추출
     */
    private Map<String, Object> extractWeatherInfo(String aiText) {
        Map<String, Object> weatherMap = new HashMap<>();

        Matcher matcher = WEATHER_JSON_PATTERNS.matcher(aiText);
        if (matcher.find()) {
            try {
                String jsonStr = matcher.group();
                logger.info("JSON 패턴 매칭 성공: {}", jsonStr);
                
                return parseJSONMap(jsonStr);
            } catch (Exception e) {
                logger.warn("JSON 파싱 실패, 다음 패턴 시도: {}", e.getMessage());
            }
        }

        return weatherMap;
    }
    
    /**
     * 날씨 요청 JSON 파싱
     */
    private Map<String, Object> parseJSONMap(String jsonStr) {
        Map<String, Object> parseMap = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            
            // 다양한 JSON 구조 지원
            if (rootNode.has("destination") && rootNode.has("date")) {
                // 직접 구조
                parseMap.put("destination", rootNode.get("destination").asText());
                parseMap.put("date", rootNode.get("date").asText());
            } else {
                throw new RuntimeException("지원하지 않는 JSON 구조");
            }
            
            // 유효성 검사
            String destination = String.valueOf(parseMap.get("destination"));
            String date = String.valueOf(parseMap.get("date"));

            logger.info("날씨 요청 파싱 성공: 목적지={}, 날짜={}", destination, date);
        } catch (Exception e) {
            logger.error("날씨 JSON 파싱 실패: {}", e.getMessage());

        }
        return parseMap;
    }
    
    /**
     * WeatherInfo JSON을 제외한 사용자 메시지 추출
     */
    private String extractUserMessage(String aiText) {
        // JSON 부분을 제거하고 나머지를 사용자 메시지로 사용
        String userMessage = aiText;
        
        Matcher matcher = WEATHER_JSON_PATTERNS.matcher(userMessage);
        if (matcher.find()) userMessage = userMessage.replaceAll(WEATHER_JSON_PATTERNS.pattern(), "").trim();
        
        // 추가 정리: 연속된 공백 제거, 불필요한 구두점 정리
        userMessage = userMessage.replaceAll("\\s+", " ").trim();
        userMessage = userMessage.replaceAll("^[,\\s]+|[,\\s]+$", ""); // 앞뒤 쉼표, 공백 제거
        
        return userMessage.isEmpty() ? "날씨 정보를 확인해드릴게요!" : userMessage;
    }
}