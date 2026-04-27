package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 驳回文章请求 DTO
 */
@Data
@Schema(description = "驳回文章请求")
public class RejectPostRequest {

    @Schema(description = "驳回原因", required = true)
    @NotBlank(message = "驳回原因不能为空")
    @Size(max = 500, message = "驳回原因不能超过500字符")
    private String reason;
}