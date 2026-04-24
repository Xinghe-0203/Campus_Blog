package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String title;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "文章内容（Markdown）", required = true)
    private String content;

    @Schema(description = "文章分类")
    private String category;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}
