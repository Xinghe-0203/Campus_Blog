package com.example.edu_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicCreateRequest {
    @NotBlank(message = "话题名称不能为空")
    @Size(min = 1, max = 50, message = "话题名称长度在1-50之间")
    private String name;

    @Size(max = 500, message = "话题描述长度不能超过500")
    private String description;
}