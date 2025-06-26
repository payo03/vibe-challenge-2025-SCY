package com.example.demo.mapper;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    // SELECT
    UserResponse loginUser(@Param("id") String id, @Param("password") String password);
    int countById(@Param("id") String id);

    // INSERT
    int insertUser(UserRequest user);
} 