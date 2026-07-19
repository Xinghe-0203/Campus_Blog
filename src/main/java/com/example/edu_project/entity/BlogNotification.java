package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_notification")
public class BlogNotification {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String type;
    private String title;
    private String content;
    private Long fromUserId;
    private Long toUserId;
    private String targetType;
    private Long targetId;
    private Integer isRead;
    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}