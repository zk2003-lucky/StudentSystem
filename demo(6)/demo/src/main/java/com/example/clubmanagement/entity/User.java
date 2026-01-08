package com.example.clubmanagement.entity;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private List<String> roles;

}