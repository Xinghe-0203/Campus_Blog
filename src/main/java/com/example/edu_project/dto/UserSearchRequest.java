package com.example.edu_project.dto;

import lombok.Data;

@Data
public class UserSearchRequest {
    private String keyword;
    private Integer page = 1;
    private Integer pageSize = 10;
}