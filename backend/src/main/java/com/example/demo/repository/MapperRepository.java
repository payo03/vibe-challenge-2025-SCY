package com.example.demo.repository;

import com.example.demo.dto.UserProfileLog;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserProfileLogMapper;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MapperRepository {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileLogMapper userProfileLogMapper;

    // SELECT
    public boolean isIdDuplicated(String id) {
        return userMapper.countById(id) > 0;
    }

    public UserResponse loginUser(String id, String password) {
        return userMapper.loginUser(id, password);
    }

    public int getMaxSeq(String id) {
        UserProfileLog log = UserProfileLog.builder()
            .userId(id)
            .yyyyMMdd(LocalDate.now())
            .build();

        return userProfileLogMapper.getMaxSeq(log);
    }

    // INSERT
    public int registerUser(UserRequest user) {
        return userMapper.registerUser(user);
    }

    public int insertLog(UserProfileLog log) {
        return userProfileLogMapper.insertLog(log);
    }
} 