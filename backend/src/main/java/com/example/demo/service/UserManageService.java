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
        promptBuilder.append("지금까지 대화내용을 바탕으로 다음 3가지의 내용을 '정해진 양식'에 따라 답변해주세요\n");
        promptBuilder.append("1. User의 여행성향은? 2. User의 나이대는? 3. User의 대화내용의 핵심 내용은?\n");
        promptBuilder.append("답변양식은 다음과 같습니다.\n");
        promptBuilder.append("여행성향 : [ex, 음식에 중점을 둔 활발형], 나이대 : [ex, 대략 20~30대], 핵심내용 : [ex, 3박 4일 여행일정을 계획중이며 금액이 차이나지 않는다면 해외를 고려중]\n");
        promptBuilder.append("대화내용이 적어 답변이 불가능할경우 'X' 라고만 답하세요");

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

            UserProfileLog profileLog = UserProfileLog.builder()
                .yyyyMMdd(LocalDate.now())
                .seq(seq)
                .userId(userId)
                .build();
        } catch (RuntimeException e) {
            logger.error("Runtime Exception: ", e);

        } catch (Exception e) {
            logger.error("Gemini API 호출 중 오류 발생: ", e);

        }
    }
}
