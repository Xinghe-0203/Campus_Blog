package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 校友圈动态实体类 (circle_post)
 * 使用 status=2 表示删除，不使用 is_deleted
 */
@Data
@TableName("blog_circle_post")
public class CirclePost implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作者用户ID
     */
    private Long userId;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 内容类型：1=图文，2=纯文本，3=转发
     */
    private Integer contentType;

    /**
     * 图片URL列表（JSON数组）
     */
    private String imageUrls;

    /**
     * 转发来源动态ID（如果是转发类型）
     */
    private Long repostId;

    /**
     * 被转发者用户ID（如果是转发类型）
     */
    private Long repostUserId;

    /**
     * 转发时添加的内容（如果是转发类型）
     */
    private String repostContent;

    /**
     * @提及的用户ID数组（JSON）
     */
    private String mentions;

    /**
     * 位置信息
     */
    private String location;

    /**
     * 标签列表（JSON数组）
     */
    private String tags;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 转发数
     */
    private Integer repostCount;

    /**
     * 是否置顶：0=否，1=是
     */
    private Integer isTop;

    /**
     * 可见性：0=公开，1=仅关注者，2=仅自己
     */
    private Integer visibility;

    /**
     * 是否允许评论：1=允许，0=不允许
     */
    private Integer allowComment;

    /**
     * 是否允许转发：1=允许，0=不允许
     */
    private Integer allowRepost;

    /**
     * 动态状态：1=正常，0=隐藏，2=已删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}