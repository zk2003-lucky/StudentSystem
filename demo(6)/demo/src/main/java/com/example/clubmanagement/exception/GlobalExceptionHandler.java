package com.example.clubmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(500).body("Internal Server Error");
    }
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(SQLException ex) {
        String errorMsg = "数据库操作失败";
        if (ex.getErrorCode() == 1062) { // MySQL唯一约束错误码
            errorMsg = "用户名已存在";
        } else if (ex.getErrorCode() == 1452) { // 外键约束失败
            errorMsg = "角色分配失败（无效用户ID）";
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", errorMsg, "code", ex.getErrorCode()));
    }
}
