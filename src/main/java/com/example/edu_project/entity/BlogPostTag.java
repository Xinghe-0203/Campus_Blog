package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章-标签关联实体类 (blog_post_tag)
 * 【说明】中间表，实现文章和标签的多对多关系
 */
@Data
@TableName("blog_post_tag")
public class BlogPostTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @TableField
    private Long postId;

    /**
     * 标签ID
     */
    @TableId(type = IdType.INPUT)
    private Long tagId;
}
