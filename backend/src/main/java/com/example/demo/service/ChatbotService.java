package com.example.demo.service;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);
    private final RestTemplate restTemplate;
    
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ChatbotService() {
        this.restTemplate = new RestTemplate();
    }

    public ChatResponse generateResponse(ChatRequest request, HttpSession session) {
        logger.info("=== Gemini API 호출 시작 ===");
        String prompt = "당신은 친근한 여행 도우미 챗봇입니다. 질문에 사용된 언어로 답변해 주세요!";
        String message = request.getMessage();

        // 세션 내 userId별 데이터 관리
        Map<String, Map<String, Object>> userSessionMap = (Map<String, Map<String, Object>>) session.getAttribute("userSessionMap");
        if (userSessionMap == null) {
            userSessionMap = new HashMap<>();
            session.setAttribute("userSessionMap", userSessionMap);
        }
        String userId = request.getUserId();
        if (!"demo-user".equals(userId)) {
            userSessionMap.putIfAbsent(userId, new HashMap<>());
            Map<String, Object> userData = userSessionMap.get(userId);
            userData.put("lastAnswer", message);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastCall = (LocalDateTime) userData.get("lastApiCall");
            Boolean alreadyInserted = (Boolean) userData.get("profileInserted");
            if (alreadyInserted == null) alreadyInserted = false;
            if (lastCall != null && !alreadyInserted && java.time.Duration.between(lastCall, now).toMinutes() >= 1) {
                // 1분 경과 후 insert (중복 insert 방지)
                // TODO: user_profile_log insert 로직 구현
                userData.put("profileInserted", true);
                // insert 완료 시 세션에서 해당 userId 데이터 제거
                userSessionMap.remove(userId);
            } else {
                userData.put("lastApiCall", now);
            }
        }

        try {
            // Gemini API 요청 본문 생성 (최신 양식)
            Map<String, Object> requestBody = new HashMap<>();

            // systemInstruction
            Map<String, Object> sysInstruction = new HashMap<>();
            sysInstruction.put("role", "system");
            sysInstruction.put("parts", List.of(Map.of("text", prompt)));
            requestBody.put("systemInstruction", sysInstruction);

            // contents
            Map<String, Object> userContent = new HashMap<>();
            userContent.put("role", "user");
            userContent.put("parts", List.of(Map.of("text", message)));
            requestBody.put("contents", List.of(userContent));
            
            logger.info("Gemini API 요청 준비 완료");

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            logger.info("Gemini API 호출 중...");
            
            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey,
                HttpMethod.POST,
                entity,
                Map.class
            );

            logger.info("Gemini API 응답 수신 완료 - 상태 코드: {}", response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                // Gemini API 응답 구조 파싱
                Map<String, Object> candidate = (Map<String, Object>) ((java.util.List<Object>) responseBody.get("candidates")).get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                List<Object> parts = (java.util.List<Object>) content.get("parts");
                Map<String, Object> part = (Map<String, Object>) parts.get(0);
                String aiResponse = (String) part.get("text");
                
                if (aiResponse == null || aiResponse.trim().isEmpty()) throw new RuntimeException("Gemini API에서 빈 응답을 받았습니다.");
                logger.info("AI 응답 추출 완료 - 길이: {}자", aiResponse.length());
                
                return createChatResponse(aiResponse, userId);
            } else {
                throw new RuntimeException("Gemini API 응답 처리 실패");
            }
            
        } catch (Exception e) {
            logger.error("Gemini API 호출 중 오류 발생: ", e);
            
            // API 오류 시 사용자에게 명확한 메시지 전달
            return createErrorResponse(userId);
        }
    }
    
    private ChatResponse createChatResponse(String message, String userId) {
        ChatResponse response = new ChatResponse();
        response.setMessage(message);
        response.setUserId(userId);
        response.setTimestamp(LocalDateTime.now());
        response.setUserMessage(false);
        return response;
    }
    
    private ChatResponse createErrorResponse(String userId) {
        ChatResponse response = new ChatResponse();
        response.setMessage("죄송합니다. AI 서비스에 일시적인 문제가 발생했습니다. Gemini API 키가 올바른지 확인해 주세요.");
        response.setUserId(userId);
        response.setTimestamp(LocalDateTime.now());
        response.setUserMessage(false);
        return response;
    }
} 