package com.example.edu_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagCreateRequest {
    @NotBlank(message = "标签名称不能为空")
    @Size(min = 1, max = 50, message = "标签名称长度在1-50之间")
    private String name;
}