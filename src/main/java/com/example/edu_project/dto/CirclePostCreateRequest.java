package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 校友圈动态创建请求 DTO
 */
@Data
@Schema(description = "校友圈动态创建请求")
public class CirclePostCreateRequest {

    @Schema(description = "动态内容")
    @NotBlank(message = "动态内容不能为空")
    @Size(max = 2000, message = "动态内容不能超过2000字符")
    private String content;

    @Schema(description = "图片URL列表")
    private List<String> images;

    @Schema(description = "位置信息")
    @Size(max = 100, message = "位置信息不能超过100字符")
    private String location;

    @Schema(description = "转发来源动态ID")
    private Long repostId;

    @Schema(description = "标签列表")
    private List<String> tags;
}