package com.example.edu_project.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailResponse {
    private Long id;
    private Long userId;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // User info
    private String nickname;
    private String avatar;

    // Tags
    private List<String> tags;

    // Interaction states
    private Boolean isLiked;
    private Boolean isCollected;
}