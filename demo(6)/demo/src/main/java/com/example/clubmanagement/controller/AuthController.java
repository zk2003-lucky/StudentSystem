package com.example.clubmanagement.controller;

import com.example.clubmanagement.security.JwtTokenProvider;
import com.example.clubmanagement.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 设置认证信息到Security上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT令牌
            String jwt = tokenProvider.generateToken(authentication);

            return ResponseEntity.ok(jwt);

        } catch (Exception e) {
            //e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE ).body("登录失败，请检查用户名和密码");
        }
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
    }

    @Data
    public static class RegisterRequest {

        private String username;

        private String password;


        private String confirmPassword;
    }



    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterRequest registerRequest) {
        Map<String, Object> response = new HashMap<>();

        // 1. 验证密码一致性
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            response.put("status", 400);
            response.put("message", "两次输入的密码不一致");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. 检查用户名是否存在
        if (userService.findByUsername(registerRequest.getUsername()) != null) {
            response.put("status", 400);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 3. 注册用户
            userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
            response.put("status", 200);
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 4. 打印详细错误日志
            e.printStackTrace();
            response.put("status", 500);
            response.put("message", "注册失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    // 注册新用户
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody LoginRequest registerRequest) {
//        userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
//        return ResponseEntity.ok("User registered successfully");
//    }

}