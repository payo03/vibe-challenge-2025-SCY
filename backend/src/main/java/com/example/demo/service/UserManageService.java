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

import com.example.demo.config.TextExctractConfig;
import com.example.demo.dto.UserProfileLog;
import com.example.demo.repository.MapperRepository;

import org.springframework.http.*;

@Component
public class UserManageService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserManageService.class);

    private final Map<String, LocalDateTime> lastActivityMap = new ConcurrentHashMap<>();

    @Value("${default.user}")
    private String defaultUser;
    
    @Autowired
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
        // 내용 요약본 생성
        promptBuilder.append("Based on the conversation so far, please judge the following three points and answer only in \"JSON format\".\n");
        promptBuilder.append("(If it is not exact, please write it down if you can estimate it)\n");
        promptBuilder.append("1. User's travel tendencies\n");
        promptBuilder.append("2. User's age\n");
        promptBuilder.append("3. Summary of the main points of the conversation\n\n");
        
        promptBuilder.append("Here is the JSON format you need to respond to:\n\n");
        
        promptBuilder.append("{\n");
        promptBuilder.append("  \"Travel tendency\": \"[ANSWER or X]\",\n");
        promptBuilder.append("  \"Age\": \"[ANSWER or X]\",\n");
        promptBuilder.append("  \"Key content\": \"[ANSWER or X]\"\n");
        promptBuilder.append("}\n\n");
        
        promptBuilder.append("If you have difficulty determining the information, please set the value of the corresponding item to \"X\".");
        

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

            // JSON 파싱
            Map<String, String> parsedMap = TextExctractConfig.parseSimpleJsonLikeString(result);

            UserProfileLog profileLog = UserProfileLog.builder()
                .yyyyMMdd(LocalDate.now())
                .seq(seq)
                .userId(userId)
                .trait(parsedMap.getOrDefault("Travel tendency", ""))
                .ageGroup(parsedMap.getOrDefault("Age", ""))
                .summarize(parsedMap.getOrDefault("Key content", ""))
                .build();
            
            mapperRepository.insertLog(profileLog);
        } catch (RuntimeException e) {
            logger.error("Runtime Exception: ", e);

        } catch (Exception e) {
            logger.error("Gemini API 호출 중 오류 발생: ", e);

        }
    }
}
