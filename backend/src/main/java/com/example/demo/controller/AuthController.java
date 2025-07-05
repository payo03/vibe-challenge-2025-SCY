package com.example.demo.controller;

import com.example.demo.config.HeaderTypeList;
import com.example.demo.dto.UserProfileLog;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.repository.MapperRepository;
import com.example.demo.service.CommonService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private static final String REGISTER_URL = "/register";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_URL = "/logout";

    private static final String MSG_ID_DUPLICATED = "가입한 이력이 있습니다.";
    private static final String MSG_REGISTER_SUCCESS = "회원가입 성공";
    private static final String MSG_REGISTER_FAIL = "회원가입 실패";
    private static final String MSG_LOGIN_SUCCESS = "로그인 성공";
    private static final String MSG_LOGIN_FAIL = "아이디 또는 비밀번호가 올바르지 않습니다.";

    @Value("${default.user}")
    private String defaultUser;

    private final CommonService commonService;
    private final MapperRepository mapperRepository;

    // 생성자 주입
    public AuthController(CommonService commonService, MapperRepository mapperRepository) {
        this.commonService = commonService;
        this.mapperRepository = mapperRepository;
    }

    // 회원가입
    @PostMapping(value = REGISTER_URL, produces = HeaderTypeList.APPLICATION_JSON_UTF8)
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        UserResponse response = new UserResponse();

        // 중복시 예외처리
        if (mapperRepository.isIdDuplicated(userRequest.getId())) {
            response = UserResponse.builder()
                    .success(false)
                    .message(MSG_ID_DUPLICATED)
                    .build();
            return ResponseEntity.ok(response);
        }

        // 회원가입
        int regResult = mapperRepository.registerUser(userRequest);
        if (regResult > 0) {
            response = UserResponse.builder()
                    .id(userRequest.getId())
                    .name(userRequest.getName())
                    .success(true)
                    .message(MSG_REGISTER_SUCCESS)
                    .build();
        } else {
            response = UserResponse.builder()
                    .success(false)
                    .message(MSG_REGISTER_FAIL)
                    .build();
        }
        return ResponseEntity.ok(response);
    }

    // 로그인
    @PostMapping(value = LOGIN_URL, produces = HeaderTypeList.APPLICATION_JSON_UTF8)
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest userRequest) {
        UserResponse dbUser = mapperRepository.loginUser(userRequest.getId(), userRequest.getPassword());

        UserResponse response = new UserResponse();
        if (dbUser != null && dbUser.getId() != null) {
            // 1. 최근 대화의 요약본 SELECT, API Request
            Map<LocalDate, List<UserProfileLog>> profileLogMap = mapperRepository.selectLogList(dbUser.getId());

            try {
                logger.info("###################### User Log ######################\n");
                logger.info("profileLogMap : {}", profileLogMap);
                logger.info("###################### User Log ######################\n");
            } catch (Exception e) {
                logger.warn("Failed to Load", e);
            }
            // 2. Return
            response = UserResponse.builder()
                    .id(dbUser.getId())
                    .name(dbUser.getName())
                    .success(true)
                    .message(MSG_LOGIN_SUCCESS)
                    .profileLogMap(profileLogMap)
                    .build();
        } else {
            response = UserResponse.builder()
                    .success(false)
                    .message(MSG_LOGIN_FAIL)
                    .build();
        }
        return ResponseEntity.ok(response);
    }

    // 로그아웃 
    @PostMapping(LOGOUT_URL)
    public ResponseEntity<Void> logout(@RequestBody UserRequest userRequest) {
        String userId = userRequest.getId();
        commonService.finishUser(userId);
        
        return ResponseEntity.ok().build();
    }

} 