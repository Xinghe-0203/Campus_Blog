package com.example.edu_project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FollowRequest {
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;
}