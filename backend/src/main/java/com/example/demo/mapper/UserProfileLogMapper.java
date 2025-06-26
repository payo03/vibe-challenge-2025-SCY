package com.example.demo.mapper;

import com.example.demo.dto.UserProfileLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileLogMapper {
    // SELECT
    int getMaxSeq(UserProfileLog log);

    // INSERT
    int insertLog(UserProfileLog log);
} 