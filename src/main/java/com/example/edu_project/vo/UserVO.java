package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息 VO（脱敏）
 * 用于返回用户信息，不包含敏感字段如密码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息（脱敏）")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 邮箱（仅用户本人或管理员可见）
     */
    private String email;

    /**
     * 角色（仅用户本人或管理员可见）
     */
    private String role;

    @Schema(description = "账号状态：1=正常，0=禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "粉丝数")
    private Integer followerCount;

    @Schema(description = "关注数")
    private Integer followingCount;

    @Schema(description = "是否关注（当前用户是否关注此用户）")
    private Boolean isFollowing;
}