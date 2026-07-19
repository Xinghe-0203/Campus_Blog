package com.example.edu_project.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MediaQueryRequest {
    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量最小为1")
    private Integer pageSize = 10;
}