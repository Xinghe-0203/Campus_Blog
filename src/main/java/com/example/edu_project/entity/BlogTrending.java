package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 趋势/热门文章实体类 (blog_trending)
 */
@Data
@TableName("blog_trending")
public class BlogTrending implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 热度评分 (score = view*1 + like*5 + comment*10)
     */
    private Integer score;

    /**
     * 阅读量快照
     */
    private Integer viewCount;

    /**
     * 点赞数快照
     */
    private Integer likeCount;

    /**
     * 评论数快照
     */
    private Integer commentCount;

    /**
     * 统计日期
     */
    private LocalDateTime statDate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除：0-正常，1-删除
     */
    @TableLogic
    private Integer isDeleted;
}