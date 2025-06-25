package com.example.demo.mapper;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int countById(@Param("id") String id);
    int insertUser(UserRequest user);
    UserResponse loginUser(@Param("id") String id, @Param("password") String password);
} 