package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应VO（支持树形结构）
 */
@Data
@Schema(description = "评论响应")
public class CommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "文章ID")
    private Long postId;

    @Schema(description = "评论者用户ID")
    private Long userId;

    @Schema(description = "评论者昵称")
    private String userNickname;

    @Schema(description = "评论者头像")
    private String userAvatar;

    @Schema(description = "父评论ID")
    private Long parentId;

    @Schema(description = "回复的用户昵称（如果是回复）")
    private String replyToNickname;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子评论列表（回复）")
    private List<CommentVO> replies;
}
