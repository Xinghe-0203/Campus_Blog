package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 关注请求DTO
 */
@Data
@Schema(description = "关注请求")
public class FollowRequest {

    @NotNull(message = "目标用户ID不能为空")
    @Schema(description = "目标用户ID", required = true)
    private Long targetUserId;
}