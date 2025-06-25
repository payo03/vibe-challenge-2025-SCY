package com.example.demo.controller;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.service.ChatbotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);
    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        logger.info("=== 챗봇 API 호출됨 ===");
        logger.info("사용자 메시지: {}", request.getMessage());
        logger.info("세션 ID: {}", request.getSessionId());
        
        try {
            ChatResponse response = chatbotService.generateResponse(request);
            logger.info("AI 응답 생성 완료: {}", response.getMessage().substring(0, Math.min(50, response.getMessage().length())) + "...");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("챗봇 API 오류 발생: ", e);
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setMessage("서버 오류가 발생했습니다.");
            errorResponse.setSessionId(request.getSessionId());
            errorResponse.setUserMessage(false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.info("헬스체크 API 호출됨");
        return ResponseEntity.ok("Chatbot API is running!");
    }
} 