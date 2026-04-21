package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类 (sys_user)
 * 【说明】
 *   对应数据库中的 sys_user 表，使用 MyBatis Plus 注解自动映射字段。
 *   使用 Lombok @Data 注解自动生成 Getter/Setter/toString/equals/hashCode。
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增长
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用户角色：user=普通用户，admin=管理员
     */
    private String role;

    /**
     * 账号状态：1=正常，0=禁用
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

    /**
     * 逻辑删除：0=正常，1=删除
     */
    @TableLogic
    private Integer isDeleted;
}
