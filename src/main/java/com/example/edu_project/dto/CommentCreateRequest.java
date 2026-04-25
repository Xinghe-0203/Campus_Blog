package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发表评论请求DTO
 */
@Data
@Schema(description = "发表评论请求")
public class CommentCreateRequest {

    @NotNull(message = "文章ID不能为空")
    @Schema(description = "文章ID", required = true)
    private Long postId;

    @Schema(description = "父评论ID（回复时必填，一级评论可空）")
    private Long parentId;

    @Schema(description = "评论内容", required = true)
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容最多1000字符")
    private String content;
}
