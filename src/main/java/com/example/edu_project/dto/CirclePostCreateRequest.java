package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Schema(description = "可见性：0=公开，1=仅关注者，2=仅自己", example = "0")
    @Min(value = 0, message = "可见性取值范围为0-2")
    @Max(value = 2, message = "可见性取值范围为0-2")
    private Integer visibility = 0;

    @Schema(description = "是否允许评论：1=允许，0=不允许", example = "1")
    @Min(value = 0, message = "allowComment取值范围为0-1")
    @Max(value = 1, message = "allowComment取值范围为0-1")
    private Integer allowComment = 1;

    @Schema(description = "是否允许转发：1=允许，0=不允许", example = "1")
    @Min(value = 0, message = "allowRepost取值范围为0-1")
    @Max(value = 1, message = "allowRepost取值范围为0-1")
    private Integer allowRepost = 1;
}