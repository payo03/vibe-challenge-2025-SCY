package com.example.demo.service;

import com.example.demo.config.HeaderTypeList;
import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.dto.ParsedResponse;
import com.example.demo.dto.UserProfileLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final CommonService commonService;
    private final RestTemplate restTemplate;
    private final WeatherAPIService weatherAPIService;
    private final ResponseParserService parserService;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiURL;

    @Value("${default.user}")
    private String defaultUser;

    // 생성자 주입
    public ChatbotService(CommonService commonService, 
                        WeatherAPIService weatherAPIService,
                        ResponseParserService parserService,
                        @Qualifier("defaultRestTemplate") RestTemplate restTemplate
    ) {
        this.commonService = commonService;
        this.weatherAPIService = weatherAPIService;
        this.parserService = parserService;
        this.restTemplate = restTemplate;
    }

    public ChatResponse generateResponse(ChatRequest request) {
        logger.info("=== Gemini API 호출 시작 ===");
        String message = request.getMessage();
        String userId = request.getUserId();
        
        try {
            // API 호출
            // 1. 헤더
            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put(HeaderTypeList.CONTENT_TYPE, HeaderTypeList.APPLICATION_JSON);

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
            String endpointURL = String.format(CommonService.ENDPOINT_FORMAT, geminiApiURL, geminiApiKey);
            String aiText = commonService.callGeminiApi(restTemplate, headerMap, requestBody, endpointURL);
            ParsedResponse response = parserService.parseGeminiResponse(aiText);
            String result = response.getUserMessage();

            // 4. 날씨요청이 있는경우 정보 추가
            Map<String, Object> weatherInfo = response.getWeatherInfo();
            if(!weatherInfo.isEmpty()) result += "\n\n" + weatherAPIService.getWeatherString(weatherInfo);

            // 5. 대화내용 저장
            Map<String, Object> conversationMap = new HashMap<>();
            conversationMap.put(CommonService.KEY_QUESTION, message);
            conversationMap.put(CommonService.KEY_ANSWER, result);

            // 6. 사용자 정보 업데이트
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

    public ChatResponse callLogSummary(String userId, UserProfileLog lastLog) {

        try {
            // API 호출
            // 1. 헤더
            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put(HeaderTypeList.CONTENT_TYPE, HeaderTypeList.APPLICATION_JSON);

            // 2. Body / Prompt, Input값을 통한 RequestBody 생성
            String message = commonService.buildLogSummary(lastLog);
            Map<String, Object> requestBody = commonService.buildRequestBody(userId, message);
            
            try {
                logger.info("###################### Gemini API Request ######################\n");
                logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody));
                logger.info("###################### Gemini API Request ######################\n");
            } catch (Exception e) {
                logger.warn("Failed to pretty print JSON", e);
            }
            
            // 3. Call Gemini
            String endpointURL = String.format(CommonService.ENDPOINT_FORMAT, geminiApiURL, geminiApiKey);
            String result = commonService.callGeminiApi(restTemplate, headerMap, requestBody, endpointURL);

            // 4. 대화내용 저장
            Map<String, Object> conversationMap = new HashMap<>();
            conversationMap.put(CommonService.KEY_QUESTION, message);
            conversationMap.put(CommonService.KEY_ANSWER, result);

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