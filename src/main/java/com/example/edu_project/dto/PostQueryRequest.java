package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文章分页查询请求 DTO
 */
@Data
@Schema(description = "文章分页查询请求")
public class PostQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "搜索关键词（标题/内容）")
    private String keyword;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "作者用户ID")
    private Long userId;

    @Schema(description = "标签ID")
    private Long tagId;
}
