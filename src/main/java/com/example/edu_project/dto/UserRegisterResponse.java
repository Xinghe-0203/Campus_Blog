package com.example.edu_project.dto;

import lombok.Data;

@Data
public class UserRegisterResponse {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String role;
    private String token;
    private String refreshToken;
}