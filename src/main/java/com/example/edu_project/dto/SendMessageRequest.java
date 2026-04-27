package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发送私信请求 DTO
 */
@Data
@Schema(description = "发送私信请求")
public class SendMessageRequest {

    @Schema(description = "接收者用户ID", required = true)
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @Schema(description = "私信内容", required = true)
    @NotNull(message = "私信内容不能为空")
    @Size(max = 2000, message = "私信内容不能超过2000字符")
    private String content;
}