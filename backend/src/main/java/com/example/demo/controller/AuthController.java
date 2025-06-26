package com.example.demo.controller;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserActiveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;

    @Autowired
    UserActiveService activeService;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/register", produces = "application/json; charset=UTF-8")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        if (userRepository.isIdDuplicated(userRequest.getId())) {
            return ResponseEntity.ok(
                UserResponse.builder()
                    .success(false)
                    .message("가입한 이력이 있습니다.")
                    .build()
            );
        }
        int regResult = userRepository.registerUser(userRequest);
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
        UserResponse dbUser = userRepository.loginUser(userRequest.getId(), userRequest.getPassword());
        if (dbUser != null && dbUser.getId() != null) {
            return ResponseEntity.ok(
                UserResponse.builder()
                    .id(dbUser.getId())
                    .name(dbUser.getName())
                    .success(true)
                    .message("로그인 성공")
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
        System.out.println("HERE");
        activeService.removeUser(userId);
        
        return ResponseEntity.ok().build();
    }
} 