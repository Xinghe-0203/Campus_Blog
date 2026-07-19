package com.example.edu_project.dto;

import lombok.Data;

@Data
public class PostQueryRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private Long userId;
    private Integer status;
    private String keyword;
    private Long tagId;
    private String sortBy;
    private String sortOrder;
}