package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.LanguageDetectorConfig;
import com.example.demo.dto.ChatResponse;

public class CommonService {

    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);
    
    public static ChatResponse createChatResponse(String userId, String message) {
        ChatResponse response = new ChatResponse();
        response.setMessage(message);
        response.setUserId(userId);
        response.setTimestamp(LocalDateTime.now());
        response.setUserMessage(false);

        return response;
    }
    
    public static ChatResponse createErrorResponse(String userId, String message) {
        ChatResponse response = new ChatResponse();
        response.setMessage("죄송합니다. AI 서비스에 일시적인 문제가 발생했습니다.\nServer : " + message);
        response.setUserId(userId);
        response.setTimestamp(LocalDateTime.now());
        response.setUserMessage(false);

        return response;
    }

    public static Map<String, Object> buildRequestBody(String message, String prompt) {
        // Default Prompt 
        String language = LanguageDetectorConfig.detectLanguage(message);
        String defaultMessage = "Rule1. Answer in" + language + ".\n";
        defaultMessage += "Rule2. You are a helpful travle assistant\n";
        defaultMessage += prompt;   // 추가적인 프롬프트

        message = defaultMessage + "\n" + message;
        return Map.of(
            "contents", List.of(
                Map.of(
                    "parts", List.of(Map.of("text", message))
                )
            )
        );
    }

    public static Map<String, Object> buildRequestBody(String message) {
        return buildRequestBody(message, "");
    }

    public static HttpHeaders buildRequestHeader(Map<String, Object> typeMap) {
        HttpHeaders header = new HttpHeaders();
        for(String key : typeMap.keySet()) {
            header.set(key, String.valueOf(typeMap.get(key)));
        }

        return header;
    }

    @SuppressWarnings({ "unchecked", "null" })
    public static String callGeminiApi(RestTemplate restTemplate, Map<String, Object> headerMap, Map<String, Object> requestBody, String endpointURL) {
        HttpHeaders headers = buildRequestHeader(headerMap);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            endpointURL,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<>() {}
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseMap = response.getBody();

            // Gemini API 응답 구조 파싱
            Map<String, Object> candidate = (Map<String, Object>) ((java.util.List<Object>) responseMap.get("candidates")).get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            List<Object> parts = (java.util.List<Object>) content.get("parts");
            Map<String, Object> part = (Map<String, Object>) parts.get(0);
            String aiText = (String) part.get("text");
            if (aiText == null || aiText.trim().isEmpty()) throw new RuntimeException("Gemini에서 빈 응답을 받았습니다.");
            logger.info(aiText);

            return aiText;
        } else {
            throw new RuntimeException("Gemini API 응답 실패: " + response.getStatusCode());
        }
    }
}
