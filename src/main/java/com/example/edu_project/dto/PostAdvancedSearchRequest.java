package com.example.edu_project.dto;

import lombok.Data;

@Data
public class PostAdvancedSearchRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long userId;
    private Long tagId;
    private String startDate;
    private String endDate;
    private String sortBy;
    private String sortOrder;
}