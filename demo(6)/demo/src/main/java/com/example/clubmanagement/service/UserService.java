package com.example.clubmanagement.service;

import com.example.clubmanagement.entity.User;
import com.example.clubmanagement.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public User findByUserId(Long userId) {
        return userMapper.findByUserId(userId);
    }

//    public void registerUser(String username, String password) {
//        String encodedPassword = passwordEncoder.encode(password);
//        if (findByUsername(username) != null) {
//            throw new IllegalArgumentException("用户名已存在");
//        }
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(encodedPassword);
//        //注册新用户
//        userMapper.insertUser(user);
//        //添加角色
//        userMapper.insertUserRole(user.getId(),"USER");
//    }

@   Transactional // 必须添加事务注解
public void registerUser(String username, String password) {
    // 创建新用户对象
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));

    // 插入用户（MyBatis会自动填充id字段）
    userMapper.insertUser(user);

    // 验证ID是否生成
    if (user.getId() == null) {
        throw new IllegalStateException("用户ID生成失败，检查：\n1. 表自增配置\n2. MyBatis映射");
    }

    // 插入用户角色（修复SQL）
    userMapper.insertUserRole(user.getId(), "USER");
}

}