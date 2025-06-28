package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.UserProfileLog;
import com.example.demo.repository.MapperRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.*;

@Component
public class UserManageService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserManageService.class);

    private final Map<String, LocalDateTime> lastActivityMap = new ConcurrentHashMap<>();

    @Value("${default.user}")
    private String defaultUser;
    
    @Qualifier("defaultRestTemplate")
    RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiURL;

    @Autowired
    MapperRepository mapperRepository;

    public void updateUserActivity(String userId, LocalDateTime time) {
        if (!defaultUser.equals(userId)) lastActivityMap.put(userId, time);
    }

    public Map<String, LocalDateTime> getAllUsers() {
        return lastActivityMap;
    }

    public void finishUser(String userId) {

        if(!defaultUser.equals(userId)) {
            this.removeUser(userId);
            this.summarize(userId);
        }
    }

    // 내부함수
    private void removeUser(String userId) {
        lastActivityMap.remove(userId);
    }

    private void summarize(String userId) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("지금까지의 대화 내용을 바탕으로 다음 3가지를 판단하여 **JSON 형식**으로만 답변해주세요.\n");
        promptBuilder.append("(정확하지 않아도 추정 가능하면 작성)\n");
        promptBuilder.append("1. User의 여행 성향\n");
        promptBuilder.append("2. User의 나이대\n");
        promptBuilder.append("3. 대화 내용의 핵심 요약\n\n");
        
        promptBuilder.append("💡 아래 형식(답변 샘플)에 **정확히 맞춰서 JSON으로만** 답변해주세요. 설명은 생략해주세요.\n\n");
        
        promptBuilder.append("{\n");
        promptBuilder.append("  \"여행성향\": \"음식에 중점을 둔 활발형\",\n");
        promptBuilder.append("  \"나이대\": \"대략 20~30대\",\n");
        promptBuilder.append("  \"핵심내용\": \"3박 4일 여행일정을 계획 중이며, 금액이 차이나지 않는다면 해외도 고려 중\"\n");
        promptBuilder.append("}\n\n");
        
        promptBuilder.append("✅ 만약 정보를 판단하기 어려운 경우에는 해당 항목 값을 \"X\"로 설정해주세요.");
        

        String message = promptBuilder.toString();
        try {
            // API 호출
            // 1. 헤더
            Map<String, Object> headerMap = new HashMap<String, Object>();
            headerMap.put("Content-Type", MediaType.APPLICATION_JSON);

            // 2. Body / Prompt, Input값을 통한 RequestBody 생성
            Map<String, Object> requestBody = CommonService.buildRequestBody(message);
            
            // 3. Endpoint URL
            StringBuilder sb = new StringBuilder();
            String endpointURL = sb.append(geminiApiURL).append("?key=").append(geminiApiKey).toString();

            String result = CommonService.callGeminiApi(restTemplate, headerMap, requestBody, endpointURL);
            Integer seq = mapperRepository.getMaxSeq(userId);

            // JSON 파싱 (Jackson 이용)
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> resultMap = mapper.readValue(result, new TypeReference<Map<String, String>>() {});

            UserProfileLog profileLog = UserProfileLog.builder()
                .yyyyMMdd(LocalDate.now())
                .seq(seq)
                .userId(userId)
                .trait(resultMap.getOrDefault("여행성향", ""))
                .ageGroup(resultMap.getOrDefault("나이대", ""))
                .summarize(resultMap.getOrDefault("핵심내용", ""))
                .build();
            
            mapperRepository.insertLog(profileLog);
        } catch (RuntimeException e) {
            logger.error("Runtime Exception: ", e);

        } catch (Exception e) {
            logger.error("Gemini API 호출 중 오류 발생: ", e);

        }
    }
}
