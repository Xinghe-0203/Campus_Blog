package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_draft")
public class BlogDraft {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String tags;
    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}