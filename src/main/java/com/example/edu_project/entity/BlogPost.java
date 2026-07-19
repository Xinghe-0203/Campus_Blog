package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_post")
public class BlogPost {
    @TableId(type = IdType.AUTO)
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
    private Integer status; // 0:待审核, 1:已发布, 2:已驳回, 3:草稿
    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}