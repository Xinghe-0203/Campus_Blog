package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_tag")
public class BlogTag {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String color;
    private Integer postCount;
    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}