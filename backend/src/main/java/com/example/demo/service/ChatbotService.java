package com.example.demo.service;

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

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    CommonService commonService;

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
        String message = request.getMessage();
        String userId = request.getUserId();
        
        try {
            // API 호출
            // 1. 헤더
            Map<String, Object> headerMap = new HashMap<String, Object>();
            headerMap.put("Content-Type", MediaType.APPLICATION_JSON);

            // 2. Body / Prompt, Input값을 통한 RequestBody 생성
            Map<String, Object> requestBody = commonService.buildRequestBody(userId, message);
            
            try {
                logger.info("###################### Gemini API Request ######################\n");
                logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody));
                logger.info("###################### Gemini API Request ######################\n");
            } catch (Exception e) {
                logger.warn("Failed to pretty print JSON", e);
            }
            
            // 3. Endpoint URL
            StringBuilder sb = new StringBuilder();
            String endpointURL = sb.append(geminiApiURL).append("?key=").append(geminiApiKey).toString();
            String result = commonService.callGeminiApi(restTemplate, headerMap, requestBody, endpointURL);

            // 4. 대화내용 저장
            Map<String, Object> conversationMap = new HashMap<String, Object>();
            conversationMap.put("question", message);
            conversationMap.put("answer", result);

            // 5. 사용자 정보 업데이트
            commonService.logHistory(userId, conversationMap);
            return commonService.createChatResponse(userId, result);
        } catch (RuntimeException e) {
            logger.error("Runtime Exception: ", e);

            return commonService.createErrorResponse(userId, e.getMessage());
        } catch (Exception e) {
            logger.error("Gemini API 호출 중 오류 발생: ", e);

            return commonService.createErrorResponse(userId, e.getMessage());
        }
    }
} 