package com.example.edu_project.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long id;
    private String type;
    private String title;
    private String content;
    private Long fromUserId;
    private Long targetType;
    private Long targetId;
    private Integer isRead;
    private LocalDateTime createTime;
    private String timeAgo;

    private UserVO fromUser;
}