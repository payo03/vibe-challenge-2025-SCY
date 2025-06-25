package com.example.demo.repository;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final UserMapper userMapper;
    
    public UserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public boolean isIdDuplicated(String id) {
        return userMapper.countById(id) > 0;
    }

    public int registerUser(UserRequest user) {
        return userMapper.insertUser(user);
    }

    public UserResponse loginUser(String id, String password) {
        return userMapper.loginUser(id, password);
    }
} 