package com.example.demo.mapper;

import com.example.demo.dto.UserProfileLog;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileLogMapper {
    // SELECT
    int getMaxSeq(UserProfileLog log);
    List<UserProfileLog> selectLogList(String userId);

    // INSERT
    int insertLog(UserProfileLog log);
} 