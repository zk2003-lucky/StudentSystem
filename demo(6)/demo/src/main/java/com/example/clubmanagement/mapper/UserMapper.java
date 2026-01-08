package com.example.clubmanagement.mapper;

import com.example.clubmanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUsername(String username);

    User findByUserId(Long userid);

    int insertUser(User user);

    void insertUserRole(@Param("userId") Long userId, @Param("role") String role);
}