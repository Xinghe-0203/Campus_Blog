package com.example.edu_project.dto;

import lombok.Data;

@Data
public class AdminPostQueryRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private Long userId;
    private Integer status;
    private String keyword;
}