package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_post_tag")
public class BlogPostTag {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;
    private Long tagId;
    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}