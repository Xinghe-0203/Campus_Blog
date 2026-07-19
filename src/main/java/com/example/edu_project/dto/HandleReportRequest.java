package com.example.edu_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HandleReportRequest {
    @NotNull(message = "处理结果不能为空")
    private Integer result;

    @NotBlank(message = "处理理由不能为空")
    private String reason;
}