package com.example.demo.repository;

import com.example.demo.dto.UserProfileLog;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserProfileLogMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MapperRepository {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileLogMapper userProfileLogMapper;

    /* ---------------------------------------------------------------------- */
    /* ------------------------------- SELECT ------------------------------- */
    /* ---------------------------------------------------------------------- */

    // UserMapper
    public boolean isIdDuplicated(String id) {
        return userMapper.countById(id) > 0;
    }

    public UserResponse loginUser(String id, String password) {
        return userMapper.loginUser(id, password);
    }

    // UserProfileLogMapper
    public int getMaxSeq(String id) {
        UserProfileLog log = UserProfileLog.builder()
            .userId(id)
            .yyyyMMdd(LocalDate.now())
            .build();

        return userProfileLogMapper.getMaxSeq(log);
    }

    public Map<LocalDate, List<UserProfileLog>> selectLogList(String userId) {
        Map<LocalDate, List<UserProfileLog>> logMap = new HashMap<LocalDate, List<UserProfileLog>>();

        List<UserProfileLog> logList = userProfileLogMapper.selectLogList(userId);
        for (UserProfileLog log : logList) {
            LocalDate key = log.getYyyyMMdd();

            logMap.computeIfAbsent(key, list -> new ArrayList<>()).add(log);
        }

        return logMap;
    }

    // 최신 로그 1건 반환 (yyyyMMdd, seq 내림차순)
    public UserProfileLog getLatestLog(String userId) {
        List<UserProfileLog> logList = userProfileLogMapper.selectLogList(userId);  // 모듈함수 재사용
        if (logList == null || logList.isEmpty()) return null;

        // yyyyMMdd, seq 내림차순 정렬 후 첫 번째 반환
        return logList.stream()
            .sorted((a, b) -> {
                int dateCompare = b.getYyyyMMdd().compareTo(a.getYyyyMMdd());
                if (dateCompare != 0) return dateCompare;
                return Integer.compare(b.getSeq(), a.getSeq());
            })
            .findFirst()
            .orElse(null);
    }

    /* ---------------------------------------------------------------------- */
    /* ------------------------------- INSERT ------------------------------- */
    /* ---------------------------------------------------------------------- */

    // UserMapper
    public int registerUser(UserRequest user) {
        return userMapper.registerUser(user);
    }

    public int insertLog(UserProfileLog log) {
        return userProfileLogMapper.insertLog(log);
    }
} 