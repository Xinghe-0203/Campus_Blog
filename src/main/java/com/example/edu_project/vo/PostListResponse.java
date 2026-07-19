package com.example.edu_project.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostListResponse {
    private Long id;
    private Long userId;
    private String title;
    private String summary;
    private String coverImage;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private LocalDateTime createTime;

    private String nickname;
    private String avatar;
    private List<String> tags;
}