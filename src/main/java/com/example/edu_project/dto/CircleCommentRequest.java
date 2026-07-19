package com.example.edu_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CircleCommentRequest {
    private Long postId;

    private Long parentId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论长度不能超过500")
    private String content;
}