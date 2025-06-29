package com.example.demo.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${default.user}")
    private String defaultUser;

    @Autowired
    CommonService commonService;

    @Autowired
    private MapperRepository mapperRepository;

    @PostMapping(value = "/register", produces = "application/json; charset=UTF-8")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        if (mapperRepository.isIdDuplicated(userRequest.getId())) {
            return ResponseEntity.ok(
                UserResponse.builder()
                    .success(false)
                    .message("가입한 이력이 있습니다.")
                    .build()
            );
        }
        int regResult = mapperRepository.registerUser(userRequest);
        if (regResult > 0) {
            return ResponseEntity.ok(
                UserResponse.builder()
                    .id(userRequest.getId())
                    .name(userRequest.getName())
                    .success(true)
                    .message("회원가입 성공")
                    .build()
            );
        } else {
            return ResponseEntity.status(500).body(
                UserResponse.builder()
                    .success(false)
                    .message("회원가입 실패")
                    .build()
            );
        }
    }

    @PostMapping(value = "/login", produces = "application/json; charset=UTF-8")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest userRequest) {
        UserResponse dbUser = mapperRepository.loginUser(userRequest.getId(), userRequest.getPassword());
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
            return ResponseEntity.ok(
                UserResponse.builder()
                    .id(dbUser.getId())
                    .name(dbUser.getName())
                    .success(true)
                    .message("로그인 성공")
                    .profileLogMap(profileLogMap)
                    .build()
            );
        } else {
            return ResponseEntity.ok(
                UserResponse.builder()
                    .success(false)
                    .message("아이디 또는 비밀번호가 올바르지 않습니다.")
                    .build()
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody UserRequest userRequest) {
        String userId = userRequest.getId();
        commonService.finishUser(userId);
        
        return ResponseEntity.ok().build();
    }
} 