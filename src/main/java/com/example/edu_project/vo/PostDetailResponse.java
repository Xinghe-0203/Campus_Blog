package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情响应 VO
 */
@Data
@Schema(description = "文章详情响应")
public class PostDetailResponse {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "作者ID")
    private Long userId;

    @Schema(description = "作者用户名")
    private String username;

    @Schema(description = "作者昵称")
    private String nickname;

    @Schema(description = "作者头像")
    private String avatar;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "文章分类")
    private String category;

    @Schema(description = "阅读量")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "收藏数")
    private Integer collectCount;

    @Schema(description = "状态：1=已发布，0=草稿，2=已下架")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "标签列表")
    private List<TagVO> tags;

    @Data
    @Schema(description = "标签信息")
    public static class TagVO {
        private Long id;
        private String name;
    }
}
