package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章/帖子实体类 (blog_post)
 */
@Data
@TableName("blog_post")
@Schema(description = "文章信息")
public class BlogPost implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "文章ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作者用户ID
     */
    @Schema(description = "作者用户ID")
    private Long userId;

    /**
     * 文章标题
     */
    @Schema(description = "文章标题")
    private String title;

    /**
     * 文章摘要
     */
    @Schema(description = "文章摘要")
    private String summary;

    /**
     * 文章内容（Markdown格式）
     */
    @Schema(description = "文章内容")
    private String content;

    /**
     * 文章分类
     */
    @Schema(description = "文章分类")
    private String category;

    /**
     * 封面图URL
     */
    @Schema(description = "封面图URL")
    private String coverUrl;

    /**
     * 阅读量
     */
    @Schema(description = "阅读量")
    private Integer viewCount;

    /**
     * 点赞数
     */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /**
     * 评论数
     */
    @Schema(description = "评论数")
    private Integer commentCount;

    /**
     * 收藏数
     */
    @Schema(description = "收藏数")
    private Integer collectCount;

    /**
     * 文章状态：1=已发布，0=草稿，2=已下架
     */
    @Schema(description = "文章状态：1=已发布，0=草稿，2=已下架")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @Schema(description = "逻辑删除：0=正常，1=删除")
    @TableLogic
    private Integer isDeleted;
}
