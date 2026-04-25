package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 校友圈动态响应 VO
 */
@Data
@Schema(description = "校友圈动态响应")
public class CirclePostVO {

    @Schema(description = "动态ID")
    private Long id;

    @Schema(description = "作者信息")
    private UserVO user;

    @Schema(description = "动态内容")
    private String content;

    @Schema(description = "内容类型：1=图文，2=纯文本，3=转发")
    private Integer contentType;

    @Schema(description = "图片URL列表")
    private List<String> imageUrls;

    @Schema(description = "转发的原动态")
    private CirclePostVO repostPost;

    @Schema(description = "位置信息")
    private String location;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "转发数")
    private Integer repostCount;

    @Schema(description = "阅读量")
    private Integer viewCount;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "是否已转发")
    private Boolean isReposted;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "时间 ago 描述")
    private String timeAgo;
}