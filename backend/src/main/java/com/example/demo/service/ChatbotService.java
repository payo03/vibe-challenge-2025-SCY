package com.example.demo.service;

import com.example.demo.config.LanguageDetectorConfig;
import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    UserManageService manageService;

    @Autowired
    @Qualifier("defaultRestTemplate")
    RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiURL;

    @Value("${default.user}")
    private String defaultUser;

    public ChatResponse generateResponse(ChatRequest request) {
        logger.info("=== Gemini API 호출 시작 ===");
        LocalDateTime now = LocalDateTime.now();
        String message = request.getMessage();
        String userId = request.getUserId();
        String language = LanguageDetectorConfig.detectLanguage(message);
        String prompt = "1. Answer in" + language + ".\n";
        prompt += "2. You are a helpful travle assistant";
        
        try {
            // API 호출
            // 1. 헤더
            Map<String, Object> headerMap = new HashMap<String, Object>();
            headerMap.put("Content-Type", MediaType.APPLICATION_JSON);

            // 2. Body / Prompt, Input값을 통한 RequestBody 생성
            Map<String, Object> requestBody = CommonService.buildRequestBody(prompt, message);
            
            try {
                logger.info("Gemini API 요청 준비 완료\n");
                logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody));
                logger.info("\n");
            } catch (Exception e) {
                logger.warn("Failed to pretty print JSON", e);
            }
            
            // 3. Endpoint URL
            StringBuilder sb = new StringBuilder();
            String endpointURL = sb.append(geminiApiURL).append("?key=").append(geminiApiKey).toString();

            String result = CommonService.callGeminiApi(restTemplate, headerMap, requestBody, endpointURL);

            // UserId의 마지막 답변시간 저장
            manageService.updateUserActivity(userId, now);
            return CommonService.createChatResponse(userId, result);
        } catch (RuntimeException e) {
            logger.error("Runtime Exception: ", e);

            return CommonService.createErrorResponse(userId, e.getMessage());
        } catch (Exception e) {
            logger.error("Gemini API 호출 중 오류 발생: ", e);

            return CommonService.createErrorResponse(userId, e.getMessage());
        }
    }
} 