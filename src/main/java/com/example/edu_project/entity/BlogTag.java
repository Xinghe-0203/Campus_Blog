package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签实体类 (blog_tag)
 */
@Data
@TableName("blog_tag")
@Schema(description = "标签信息")
public class BlogTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "标签ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称（唯一）
     */
    @Schema(description = "标签名称")
    private String name;

    /**
     * 文章数量
     */
    @Schema(description = "文章数量")
    private Integer postCount;

    /**
     * 逻辑删除：0-正常，1-删除
     */
    @Schema(description = "逻辑删除：0-正常，1-删除")
    @TableLogic
    private Integer isDeleted;
}
