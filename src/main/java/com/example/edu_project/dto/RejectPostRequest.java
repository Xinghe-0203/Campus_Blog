package com.example.edu_project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectPostRequest {
    @NotBlank(message = "驳回原因不能为空")
    private String reason;
}