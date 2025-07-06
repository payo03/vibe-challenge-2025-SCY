package com.example.demo.service;

import com.example.demo.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommonServiceTest {
    CommonService commonService;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commonService = new CommonService();
    }

    @Test
    void testCreateChatResponse() {
        ChatResponse res = commonService.createChatResponse("user1", "hello");
        assertEquals("user1", res.getUserId());
        assertEquals("hello", res.getMessage());
        assertFalse(res.isUserMessage());
        assertNotNull(res.getTimestamp());
    }

    @Test
    void testCreateErrorResponse() {
        ChatResponse res = commonService.createErrorResponse("user2", "error");
        assertEquals("user2", res.getUserId());
        assertTrue(res.getMessage().contains("error"));
        assertFalse(res.isUserMessage());
    }

    @Test
    void testBuildRequestHeader() {
        Map<String, Object> typeMap = new HashMap<>();
        typeMap.put("Content-Type", "application/json");
        HttpHeaders headers = commonService.buildRequestHeader(typeMap);
        assertEquals("application/json", headers.getFirst("Content-Type"));
    }

    @Test
    void testBuildRequestBody() {
        Map<String, Object> body = commonService.buildRequestBody("user1", "메시지", null);
        assertTrue(body.containsKey("contents"));
    }

    // callGeminiApi, logHistory 등은 실제 외부 의존성/상태가 필요하므로 별도 통합테스트에서 검증 권장
} 