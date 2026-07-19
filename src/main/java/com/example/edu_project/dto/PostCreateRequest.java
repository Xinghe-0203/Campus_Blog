package com.example.edu_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class PostCreateRequest {
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200")
    private String title;

    private String summary;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String coverImage;

    private List<Long> tagIds;
}