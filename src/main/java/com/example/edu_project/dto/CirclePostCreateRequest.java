package com.example.edu_project.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CirclePostCreateRequest {
    @NotBlank(message = "内容不能为空")
    @Size(max = 2000, message = "内容长度不能超过2000")
    private String content;

    @JsonAlias({"imageUrls", "images"})
    private List<String> imageUrls;

    private String visibility; // 'public', 'followers', 'private'

    private Long topicId;
}