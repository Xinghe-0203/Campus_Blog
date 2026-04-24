package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 文章创建/更新请求 DTO
 */
@Data
@Schema(description = "文章创建/更新请求")
public class PostCreateRequest {

    @Schema(description = "文章ID（更新时需要）")
    private Long id;

    @Schema(description = "文章标题", required = true)
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题不能超过200字符")
    private String title;

    @Schema(description = "文章摘要")
    @Size(max = 500, message = "文章摘要不能超过500字符")
    private String summary;

    @Schema(description = "文章内容（Markdown）", required = true)
    @NotBlank(message = "文章内容不能为空")
    @Size(max = 50000, message = "文章内容不能超过50000字符")
    private String content;

    @Schema(description = "文章分类")
    private String category;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}
