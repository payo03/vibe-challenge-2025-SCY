package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.LanguageDetectorConfig;
import com.example.demo.config.TextExctractConfig;
import com.example.demo.dto.ChatResponse;
import com.example.demo.dto.UserProfileLog;
import com.example.demo.repository.MapperRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String ENDPOINT_FORMAT = "%s?key=%s";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_ANSWER = "answer";

    /*  Question-Answer, 활동시간 기록 맵
        1Level : Key : UserId[String], Value: Info[Object] (lastActiveTime, question, answer)
        2Level
            2-1. Key : "lastActiveTime", Value: Time[LocalDateTime]
            2-2. Key : "conversation", Value: ConversationInfo[Map<String, Object>]
    */
    private final Map<String, Map<String, Object>> userInfoMap = new ConcurrentHashMap<>();

    @Autowired
    MapperRepository mapperRepository;

    @Value("${default.user}")
    private String defaultUser;
    
    @Autowired
    @Qualifier("defaultRestTemplate")
    RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiURL;
    
    /* ------------------------------------------------------------------------------------ */
    /* ------------------------------------- Response ------------------------------------- */
    /* ------------------------------------------------------------------------------------ */
    public ChatResponse createChatResponse(String userId, String message) {
        ChatResponse response = new ChatResponse();
        response.setMessage(message);
        response.setUserId(userId);
        response.setTimestamp(LocalDateTime.now());
        response.setUserMessage(false);

        return response;
    }
    
    public ChatResponse createErrorResponse(String userId, String message) {
        ChatResponse response = new ChatResponse();
        response.setMessage("죄송합니다. AI 서비스에 일시적인 문제가 발생했습니다.\nServer : " + message);
        response.setUserId(userId);
        response.setTimestamp(LocalDateTime.now());
        response.setUserMessage(false);

        return response;
    }

    /* ------------------------------------------------------------------------------------ */
    /* -------------------------------- Header, Body Create -------------------------------- */
    /* ------------------------------------------------------------------------------------ */
    public HttpHeaders buildRequestHeader(Map<String, Object> typeMap) {
        HttpHeaders header = new HttpHeaders();
        for(String key : typeMap.keySet()) {
            header.set(key, String.valueOf(typeMap.get(key)));
        }

        return header;
    }

        
    /**
     * 사용자의 대화 이력과 현재 메시지를 기반으로 Gemini API 호출에 사용할 요청 본문(request body)을 생성.
     *  1. 첫 번째 요소로 프롬프트 메시지(시스템 안내 역할)를 추가합니다
     *  2. 사용자의 이전 대화 이력이 존재하면, 각 대화 내용을 순서대로 추가합니다
     *  3. 마지막 요소로 현재 사용자의 메시지를 추가합니다
     * @param userId         사용자의 고유 식별자
     * @param requestMessage 사용자의 현재 메시지
     * @param requestPrompt  (선택) 사용할 프롬프트 메시지, null인 경우 언어 감지 결과를 바탕으로 기본 프롬프트를 생성.
     * @return Gemini API에 전달할 요청 본문을 나타내는 Map 객체
     */
    public Map<String, Object> buildRequestBody(String userId, String requestMessage, String requestPrompt) {
        // 유저의 대화내용 API 호출
        List<Map<String, Object>> contentsList = new ArrayList<>();

        // 1. 프롬프트 문자열 (시스템 안내 역할)
        String language = LanguageDetectorConfig.detectLanguage(requestMessage);
        String promptMessage = requestPrompt;
        
        if (promptMessage == null) promptMessage = createDefaultPrompt(language);
        contentsList.add(Map.of(
            "role", "user",
            "parts", List.of(Map.of("text", promptMessage))
        ));

        // 2. 이전 대화가 있다면, 순서대로 추가
        contentsList.addAll(getPreviousConversation(userId));

        // 3. user message 추가
        contentsList.add(Map.of(
            "role", "user",
            "parts", List.of(Map.of("text", requestMessage))
        ));

        // 4. 전체 request body 반환
        return Map.of("contents", contentsList);
    }

    public Map<String, Object> buildRequestBody(String userId, String message)      { return buildRequestBody(userId, message, null); }

    /* ------------------------------------------------------------------------------------ */
    /* ------------------------------------- Call API ------------------------------------- */
    /* ------------------------------------------------------------------------------------ */
    @SuppressWarnings({ "unchecked", "null" })
    public String callGeminiApi(RestTemplate restTemplate, Map<String, Object> headerMap, Map<String, Object> requestBody, String endpointURL) {
        HttpHeaders headers = buildRequestHeader(headerMap);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            endpointURL,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<>() {}
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseMap = response.getBody();

            logger.info("###################### RESPONSE ######################");
            logger.info("Response : {}", response.getBody());

            // Gemini API 응답 구조 파싱
            Map<String, Object> candidate = (Map<String, Object>) ((java.util.List<Object>) responseMap.get("candidates")).get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            List<Object> parts = (java.util.List<Object>) content.get("parts");
            Map<String, Object> part = (Map<String, Object>) parts.get(0);
            String aiText = (String) part.get("text");
            if (aiText == null || aiText.trim().isEmpty()) throw new RuntimeException("Gemini에서 빈 응답을 받았습니다.");

            logger.info("Text : {}", aiText);
            logger.info("###################### RESPONSE ######################");

            return aiText;
        } else {
            throw new RuntimeException("Gemini API 응답 실패: " + response.getStatusCode());
        }
    }

    /* ------------------------------------------------------------------------------------ */
    /* ------------------------------------- 변수 관리 ------------------------------------- */
    /* ------------------------------------------------------------------------------------ */
    @SuppressWarnings("unchecked")
    public void logHistory(String userId, Map<String, Object> inputMap) {
        String question = String.valueOf(inputMap.get(KEY_QUESTION));
        String answer = String.valueOf(inputMap.get(KEY_ANSWER));
        LocalDateTime time = LocalDateTime.now();

        Map<String, Object> userObject = userInfoMap.containsKey(userId)
            ? (Map<String, Object>) userInfoMap.get(userId)
            : new HashMap<>();

        // 1. 마지막 활동 시간 업데이트
        if (!defaultUser.equals(userId)) {
            userObject.put("lastActiveTime", time);
        }

        // 2. 대화 내용 업데이트
        List<Map<String, String>> conversationList = userObject.containsKey("conversation")
            ? (List<Map<String, String>>) userObject.get("conversation")
            : new ArrayList<>();

        // 역할 기반 메시지 추가
        conversationList.add(
            Map.of(
                "role", "user", 
                "text", question
            )
        );
        conversationList.add(
            Map.of(
                "role", "model", 
                "text", answer
            )
        );

        userObject.put("conversation", conversationList);
        userInfoMap.put(userId, userObject);
    }

    @Async
    public void finishUser(String userId) {
        // 1. 요약본 생성
        List<Map<String, Object>> previousMessages = getPreviousConversation(userId);
        if(!defaultUser.equals(userId) && previousMessages.size() >= 3) this.summarize(userId);

        // 2. 유저 정보 제거
        this.removeUser(userId);
    }

    public Map<String, Object> getUserInfo(String userId) {
        return userInfoMap.get(userId);
    }

    public Map<String, Map<String, Object>> getAllUsers() {
        return userInfoMap;
    }

    /* ------------------------------------------------------------------------------------ */
    /* -------------------------------------- Prompt -------------------------------------- */
    /* ------------------------------------------------------------------------------------ */
    // 요약본 Prompt 생성
    public String createSummaryPrompt() {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Based on the conversation so far, please judge the following points and answer only in \"JSON format\".\n");
        promptBuilder.append("(If the information is unclear or missing, please write \"X\")\n\n");
        
        promptBuilder.append("1. User's travel tendencies\n");
        promptBuilder.append("2. User's age\n");
        promptBuilder.append("3. Summary of the main points of the conversation\n");
        promptBuilder.append("4. User's expected travel date or period (if mentioned)\n");
        promptBuilder.append("5. User's intended travel destination(s) (if mentioned)\n\n");
        
        promptBuilder.append("Respond using the following JSON format:\n\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"Travel tendency\": \"[ANSWER or X]\",\n");
        promptBuilder.append("  \"Age\": \"[ANSWER or X]\",\n");
        promptBuilder.append("  \"Key content\": \"[ANSWER or X]\",\n");
        promptBuilder.append("  \"Travel date\": \"[ANSWER(FORM : \"yyyy-MM-dd\", \"yyyy-MM-dd\") or X]\",\n");
        promptBuilder.append("  \"Destination\": \"[ANSWER(FORM : City available for called with OpenCage API) or X]\"\n");
        promptBuilder.append("}\n\n");
        
        promptBuilder.append("IMPORTANT: Please write the values in the user's language used in the conversation (e.g., if the user spoke in Korean, answer in Korean).\n");
        promptBuilder.append("If you are unable to determine a certain item, set its value to \"X\".");

        return promptBuilder.toString();
    }

    // Default Prompt 생성
    public String createDefaultPrompt(String language) {
        /*
         *  Prompt 설명
         *  Rule1. 사용자 언어로 답변
         *  Rule2. 여행 도우미 역할 수행
         *  Rule3. 이전 대화는 참고용, 반복 금지
         *  Rule4. 여행과 무관한 질문 시 유머러스하게 거절
         *  Rule5. 여행지와 날짜가 명확한 경우 날씨 JSON 메타데이터 추가
         *  Rule6. 항상 관련된 후속 질문으로 답변 마무리
         *  Rule7. 여행 계획 시 일별 샘플 일정 제공
         *  Rule8. 모호한 질문 시 구체적인 질문으로 명확화
         *  Rule9. 마크다운 요소를 활용한 명확한 포맷팅
         */
        int ruleNo = 1;
        
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Rule").append(ruleNo++).append(". Answer in ").append(language).append(".\n")
            .append("Rule").append(ruleNo++).append(". You are a professional and friendly travel assistant.\n")
            .append("Rule").append(ruleNo++).append(". Always respond to the user's most recent message, but maintain conversation flow by using previous context when needed (e.g., if the user changes their plan, revise your earlier suggestion accordingly).\n")
            .append("Rule").append(ruleNo++).append(". If the message is unrelated to travel, reply humorously and do not answer seriously.\n")
            .append("Rule").append(ruleNo++).append(". Only if both destination AND date are explicitly mentioned by the user, append the following HTML-style comment (invisible to user):\n")
            .append("<!--weather:{\"destination\": \"[CITY_NAME]\", \"date\": \"[YYYY-MM-DD, ...]\"}-->\n")
            .append("Note: City must be supported by OpenCage API and date must follow yyyy-MM-dd format (comma-separated).\n")
            .append("Note: Don't guess or assume the date and city. The date and city must be clearly stated in your message.\n")
            .append("Rule").append(ruleNo++).append(". Always end your visible message with a relevant follow-up question.\n")
            .append("Rule").append(ruleNo++).append(". For trip plans (e.g., '3 days in Paris'), provide a sample itinerary by day (Day 1, Day 2, Day 3...).\n")
            .append("Rule").append(ruleNo++).append(". If the question is vague (e.g., 'Recommend a trip to Europe'), ask clarifying questions such as preferred region, budget, or travel duration.\n")
            .append("Rule").append(ruleNo++).append(". Format your reply clearly, using markdown elements such as bullet points or tables where helpful.\n");

        
        return promptBuilder.toString();
    }

    // 요약본 Prompt 질문 생성
    public String buildLogSummary(UserProfileLog lastLog) {
        String logDate = String.valueOf(lastLog.getYyyyMMdd());
        String trait = lastLog.getTrait();
        String ageGroup = lastLog.getAgeGroup();
        String summarize = lastLog.getSummarize();
        String travelDate = lastLog.getTravelDate();
        String destination = lastLog.getDestination();
        String language = LanguageDetectorConfig.detectLanguage(summarize);

        Integer infoCount = 3;
        if(!"X".equals(travelDate)) infoCount++;
        if(!"X".equals(destination)) infoCount++;

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Answer in " + language + " AND With appropriate line breaks and age-appropriate tone for the user.\n")
                    .append("Information(" + infoCount + " row) -> \n")
                    .append("Travel tendency: " + trait + ".\n")
                    .append("Age: " + ageGroup + ".\n")
                    .append("Conversation Summary: " + summarize + "\n");

        if(!"X".equals(travelDate)) promptBuilder.append("Date of travel: " + travelDate + "\n");
        if(!"X".equals(destination)) promptBuilder.append("Destination: " + destination + "\n");

        promptBuilder.append("Please bring up the topic naturally today, recalling our past conversation on " + logDate + ".\n")
                    .append("Start the travel consultation again in a friendly and gentle tone, building on the previous discussion.\n")
                    .append("Avoid repeating what was already discussed, and instead, continue the conversation based on that content.\n");

        return promptBuilder.toString();
    }


    /* ------------------------------------- 내부함수 ------------------------------------- */
    // User 정보 제거
    private void removeUser(String userId) {
        userInfoMap.remove(userId);
    }

    // 이전대화내용 Get
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getPreviousConversation(String userId) {
        List<Map<String, Object>> contents = new ArrayList<>();

        Map<String, Object> infoMap = getUserInfo(userId);
        if (infoMap != null && infoMap.containsKey("conversation")) {
            List<Map<String, String>> conversationList = (List<Map<String, String>>) infoMap.get("conversation");

            for (Map<String, String> msg : conversationList) {
                contents.add(Map.of(
                    "role", msg.get("role"),
                    "parts", List.of(Map.of("text", msg.get("text")))
                ));
            }
        }

        return contents;
    }

    // 내용 요약본 생성
    private void summarize(String userId) {
        String message = createSummaryPrompt();

        try {
            // API 호출
            // 1. 헤더
            Map<String, Object> headerMap = new HashMap<String, Object>();
            headerMap.put("Content-Type", MediaType.APPLICATION_JSON);

            // 2. Body / Prompt, Input값을 통한 RequestBody 생성
            Map<String, Object> requestBody = buildRequestBody(userId, message);
            logger.info("###################### Gemini API 요약본 Request ######################\n");
            logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody));
            logger.info("###################### Gemini API 요약본 Request ######################\n");

            // 3. Call Gemini
            String endpointURL = String.format(CommonService.ENDPOINT_FORMAT, geminiApiURL, geminiApiKey);
            String result = callGeminiApi(restTemplate, headerMap, requestBody, endpointURL);
            Integer seq = mapperRepository.getMaxSeq(userId);

            // JSON 파싱
            Map<String, String> parsedMap = TextExctractConfig.parseSimpleJsonLikeString(result);

            UserProfileLog profileLog = UserProfileLog.builder()
                .yyyyMMdd(LocalDate.now())
                .seq(seq)
                .userId(userId)
                .trait(parsedMap.getOrDefault("Travel tendency", "X"))
                .ageGroup(parsedMap.getOrDefault("Age", "X"))
                .summarize(parsedMap.getOrDefault("Key content", "X"))
                .travelDate(parsedMap.getOrDefault("Travel date", "X"))
                .destination(parsedMap.getOrDefault("Destination", "X"))
                .build();
            
            mapperRepository.insertLog(profileLog);
        } catch (RuntimeException e) {
            logger.error("Runtime Exception: ", e);

        } catch (Exception e) {
            logger.error("Gemini API 호출 중 오류 발생: ", e);

        }
    }

}
