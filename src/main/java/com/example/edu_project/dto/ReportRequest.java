package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 举报请求 DTO
 */
@Data
@Schema(description = "举报请求")
public class ReportRequest {

    @Schema(description = "举报目标类型：post=文章, comment=评论, user=用户", required = true)
    @NotBlank(message = "举报目标类型不能为空")
    private String targetType;

    @Schema(description = "举报目标ID", required = true)
    @NotNull(message = "举报目标ID不能为空")
    private Long targetId;

    @Schema(description = "举报原因", required = true)
    @NotBlank(message = "举报原因不能为空")
    @Size(max = 1000, message = "举报原因不能超过1000字符")
    private String reason;
}