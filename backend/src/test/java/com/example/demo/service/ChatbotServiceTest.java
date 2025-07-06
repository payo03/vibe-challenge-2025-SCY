package com.example.demo.service;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.dto.ParsedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ChatbotServiceTest {
    @InjectMocks
    ChatbotService chatbotService;
    @Mock
    CommonService commonService;
    @Mock
    WeatherAPIService weatherAPIService;
    @Mock
    ResponseParserService parserService;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateResponse_error() {
        ChatRequest req = new ChatRequest();
        req.setUserId("user1");
        req.setMessage("test");
        ChatResponse errorResponse = new ChatResponse();
        errorResponse.setUserId("user1");
        errorResponse.setMessage("죄송합니다. AI 서비스에 일시적인 문제가 발생했습니다.\nServer : error");
        Mockito.when(commonService.buildRequestBody(Mockito.any(), Mockito.any()))
               .thenThrow(new RuntimeException("error"));
        Mockito.when(commonService.createErrorResponse(Mockito.any(), Mockito.any()))
               .thenReturn(errorResponse);

        ChatResponse res = chatbotService.generateResponse(req);
        assertNotNull(res);
        assertEquals("user1", res.getUserId());
        assertTrue(res.getMessage().contains("error"));
    }

    @Test
    void testGenerateResponse_normal() {
        ChatRequest req = new ChatRequest();
        req.setUserId("user1");
        req.setMessage("test");
        Mockito.when(commonService.buildRequestBody(Mockito.any(), Mockito.any())).thenReturn(new HashMap<>());
        Mockito.when(commonService.callGeminiApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("aiText");
        Mockito.when(parserService.parseGeminiResponse(Mockito.any())).thenReturn(new ParsedResponse("AI응답", new HashMap<>()));
        Mockito.when(commonService.createChatResponse(Mockito.any(), Mockito.any())).thenReturn(new ChatResponse());
        Mockito.doNothing().when(commonService).logHistory(Mockito.any(), Mockito.any());

        ChatResponse res = chatbotService.generateResponse(req);
        assertNotNull(res);
    }
}