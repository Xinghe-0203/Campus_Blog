package com.example.edu_project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BanRequest {
    @NotNull(message = "ban状态不能为空")
    private Boolean ban;
}