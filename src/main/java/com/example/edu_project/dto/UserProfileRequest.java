package com.example.edu_project.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String nickname;
    private String avatar;
    private String email;
    private String bio;
}