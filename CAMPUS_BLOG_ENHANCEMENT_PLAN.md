# 校园博客论坛系统 - 功能增强计划书

> 校园博客论坛系统 - 功能增强模块
> 版本：v1.17
> 日期：2026-04-25
> 状态：📋 规划中（验证修复版）

---

## 一、现有功能分析

### 1.1 已实现功能

| 模块 | 功能 | 状态 |
|------|------|------|
| **用户模块** | 注册、登录、Token刷新、登录失败锁定 | ✅ |
| **文章模块** | CRUD、分类、标签、分页、搜索 | ✅ |
| **评论模块** | 发表评论、嵌套回复、删除 | ✅ |
| **互动模块** | 点赞、收藏 | ✅ |
| **前端** | 暗色主题、表单验证、Toast提示 | ✅ |
| **媒体模块** | 图片/视频上传（见 MEDIA_UPLOAD_PLAN.md） | 📋 |

### 1.2 缺失功能（对比微博/X）

| 类别 | 缺失功能 | 优先级 |
|------|----------|--------|
| **社交** | 关注/粉丝系统 | P0 |
| **社交** | 社交动态Feed | P0 |
| **社交** | @提及功能 | P1 |
| **社交** | #话题标签 | P1 |
| **通知** | 站内通知系统 | P0 |
| **通知** | 未读数标记 | P1 |
| **内容** | 草稿自动保存 | P1 |
| **内容** | 内容预览 | P2 |
| **搜索** | 热门/趋势内容 | P1 |
| **搜索** | 用户搜索 | P1 |
| **搜索** | 高级搜索 | P2 |
| **用户** | 修改密码 | P1 |
| **用户** | 用户封禁 | P0 |
| **用户** | 举报管理 | P1 |
| **私信** | 私信功能 | P2 |
| **体验** | 无限滚动 | P2 |
| **体验** | PWA支持 | P3 |
| **邮件** | 邮件验证 | P2 |
| **邮件** | 密码重置 | P2 |

---

## 二、社交系统

### 2.1 数据库设计 - 关注关系表

```sql
-- 关注关系表
CREATE TABLE `blog_follow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `follower_id` BIGINT NOT NULL COMMENT '关注者用户ID',
  `following_id` BIGINT NOT NULL COMMENT '被关注者用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_follower_following` (`follower_id`, `following_id`),
  INDEX `idx_following_id` (`following_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注关系表';
```

### 2.2 实体类 - BlogFollow.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关注关系实体
 */
@Data
@TableName("blog_follow")
public class BlogFollow implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关注者用户ID
     */
    private Long followerId;

    /**
     * 被关注者用户ID
     */
    private Long followingId;

    /**
     * 关注时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
}
```

### 2.3 DTO - FollowRequest.java

```java
package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "关注请求")
public class FollowRequest {
    @Schema(description = "目标用户ID")
    private Long targetUserId;
}
```

### 2.4 VO - UserVO.java 扩展

```java
// 在原有 UserVO 中添加
@Schema(description = "粉丝数")
private Integer followerCount;

@Schema(description = "关注数")
private Integer followingCount;

@Schema(description = "是否已关注")
private Boolean isFollowing;
```

### 2.5 Mapper - BlogFollowMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogFollow;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogFollowMapper extends BaseMapper<BlogFollow> {
}
```

### 2.6 Service 接口 - FollowService.java

```java
package com.example.edu_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.entity.BlogFollow;
import com.example.edu_project.vo.UserVO;

import java.util.List;

public interface FollowService extends IService<BlogFollow> {

    /**
     * 关注用户
     */
    void follow(Long followerId, Long followingId);

    /**
     * 取消关注
     */
    void unfollow(Long followerId, Long followingId);

    /**
     * 检查是否关注
     */
    boolean isFollowing(Long followerId, Long followingId);

    /**
     * 获取用户的粉丝列表
     */
    List<UserVO> getFollowers(Long userId, int page, int pageSize);

    /**
     * 获取用户关注的列表
     */
    List<UserVO> getFollowing(Long userId, int page, int pageSize);

    /**
     * 获取粉丝数量
     */
    int getFollowerCount(Long userId);

    /**
     * 获取关注数量
     */
    int getFollowingCount(Long userId);
}
```

### 2.6 Service 实现 - FollowServiceImpl.java

```java
package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogFollow;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogFollowMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.FollowService;
import com.example.edu_project.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;

@Service
public class FollowServiceImpl extends ServiceImpl<BlogFollowMapper, BlogFollow>
        implements FollowService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(400, "不能关注自己");
        }

        // 检查目标用户是否存在
        SysUser user = sysUserMapper.selectById(followingId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 检查是否已关注
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowerId, followerId)
               .eq(BlogFollow::getFollowingId, followingId);

        if (this.count(wrapper) > 0) {
            throw new BusinessException(400, "已关注该用户");
        }

        BlogFollow follow = new BlogFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        try {
            this.save(follow);
        } catch (DuplicateKeyException e) {
            // 并发情况下可能重复，视为已关注
            throw new BusinessException(400, "已关注该用户");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollow(Long followerId, Long followingId) {
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowerId, followerId)
               .eq(BlogFollow::getFollowingId, followingId);
        this.remove(wrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowerId, followerId)
               .eq(BlogFollow::getFollowingId, followingId);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVO> getFollowers(Long userId, int page, int pageSize) {
        // 查询粉丝
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowingId, userId)
               .orderByDesc(BlogFollow::getCreateTime);

        List<BlogFollow> follows = this.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                wrapper
        ).getRecords();

        if (follows.isEmpty()) {
            return List.of();
        }

        // 获取用户信息
        List<Long> userIds = follows.stream()
                .map(BlogFollow::getFollowerId)
                .collect(Collectors.toList());

        return sysUserMapper.selectBatchIds(userIds).stream()
                .map(this::convertToUserVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVO> getFollowing(Long userId, int page, int pageSize) {
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowerId, userId)
               .orderByDesc(BlogFollow::getCreateTime);

        List<BlogFollow> follows = this.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                wrapper
        ).getRecords();

        if (follows.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = follows.stream()
                .map(BlogFollow::getFollowingId)
                .collect(Collectors.toList());

        return sysUserMapper.selectBatchIds(userIds).stream()
                .map(this::convertToUserVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int getFollowerCount(Long userId) {
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowingId, userId);
        return (int) this.count(wrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public int getFollowingCount(Long userId) {
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowerId, userId);
        return (int) this.count(wrapper);
    }

    private UserVO convertToUserVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        return vo;
    }
}
```

### 2.7 Controller - FollowController.java

```java
package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.FollowService;
import com.example.edu_project.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "关注管理", description = "关注/粉丝相关接口")
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Operation(summary = "关注用户")
    @PostMapping
    public Result<Void> follow(@RequestBody FollowRequest request) {
        Long currentUserId = getCurrentUserId();
        followService.follow(currentUserId, request.getTargetUserId());
        return Result.success();
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/{targetUserId}")
    public Result<Void> unfollow(
            @Parameter(description = "目标用户ID")
            @PathVariable Long targetUserId) {
        Long currentUserId = getCurrentUserId();
        followService.unfollow(currentUserId, targetUserId);
        return Result.success();
    }

    @Operation(summary = "检查是否关注")
    @GetMapping("/check/{targetUserId}")
    public Result<Boolean> checkFollow(
            @Parameter(description = "目标用户ID")
            @PathVariable Long targetUserId) {
        Long currentUserId = getCurrentUserId();
        boolean isFollowing = followService.isFollowing(currentUserId, targetUserId);
        return Result.success(isFollowing);
    }

    @Operation(summary = "获取粉丝列表")
    @GetMapping("/followers/{userId}")
    public Result<List<UserVO>> getFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(followService.getFollowers(userId, page, pageSize));
    }

    @Operation(summary = "获取关注列表")
    @GetMapping("/following/{userId}")
    public Result<List<UserVO>> getFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(followService.getFollowing(userId, page, pageSize));
    }

    private Long getCurrentUserId() {
        // 从 SecurityUtils 获取当前用户ID
        Long userId = com.example.edu_project.utils.SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
```

---

## 三、通知系统

### 3.1 数据库设计 - 通知表

```sql
-- 通知表
CREATE TABLE `blog_notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '通知所属用户ID',
  `type` VARCHAR(50) NOT NULL COMMENT '通知类型：follow/like/comment/mention/system',
  `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '通知内容',
  `from_user_id` BIGINT DEFAULT NULL COMMENT '触发通知的用户ID',
  `target_type` VARCHAR(50) DEFAULT NULL COMMENT '关联目标类型：post/comment',
  `target_id` BIGINT DEFAULT NULL COMMENT '关联目标ID',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_user_unread` (`user_id`, `is_read`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
```

### 3.2 实体类 - BlogNotification.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("blog_notification")
public class BlogNotification implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 通知所属用户ID */
    private Long userId;

    /** 通知类型：follow/like/comment/mention/system */
    private String type;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 触发通知的用户ID */
    private Long fromUserId;

    /** 关联目标类型：post/comment */
    private String targetType;

    /** 关联目标ID */
    private Long targetId;

    /** 是否已读：0=未读，1=已读 */
    private Integer isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer isDeleted;
}
```

### 3.3 VO - NotificationVO.java

```java
package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "通知信息")
public class NotificationVO implements Serializable {

    private Long id;

    /** 通知类型 */
    private String type;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 触发通知的用户信息 */
    private UserVO fromUser;

    /** 关联目标类型 */
    private String targetType;

    /** 关联目标ID */
    private Long targetId;

    /** 是否已读 */
    private Boolean isRead;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 相对时间 */
    private String timeAgo;
}
```

### 3.4 Mapper - BlogNotificationMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogNotification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogNotificationMapper extends BaseMapper<BlogNotification> {
}
```

### 3.5 Service - NotificationService.java

```java
package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.entity.BlogNotification;
import com.example.edu_project.vo.NotificationVO;

public interface NotificationService extends IService<BlogNotification> {

    /**
     * 创建通知
     */
    void createNotification(Long userId, String type, String title,
                           String content, Long fromUserId,
                           String targetType, Long targetId);

    /**
     * 获取用户通知列表（分页）
     */
    IPage<NotificationVO> getUserNotifications(Long userId, int page, int pageSize);

    /**
     * 获取未读通知数量
     */
    int getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long notificationId, Long userId);
}
```

### 3.5 Service 实现 - NotificationServiceImpl.java

```java
package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogNotification;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogNotificationMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.NotificationService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.NotificationVO;
import com.example.edu_project.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<BlogNotificationMapper, BlogNotification>
        implements NotificationService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNotification(Long userId, String type, String title,
                                   String content, Long fromUserId,
                                   String targetType, Long targetId) {
        // 不通知自己
        if (userId.equals(fromUserId)) {
            return;
        }

        BlogNotification notification = new BlogNotification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setFromUserId(fromUserId);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setIsRead(0);
        this.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<NotificationVO> getUserNotifications(Long userId, int page, int pageSize) {
        Page<BlogNotification> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<BlogNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogNotification::getUserId, userId)
               .orderByDesc(BlogNotification::getCreateTime);

        Page<BlogNotification> result = this.page(pageParam, wrapper);

        // 转换为 VO
        Page<NotificationVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<BlogNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogNotification::getUserId, userId)
               .eq(BlogNotification::getIsRead, 0);
        return (int) this.count(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId, Long userId) {
        BlogNotification notification = this.getById(notificationId);
        if (notification == null) {
            throw new BusinessException(404, "通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作");
        }
        notification.setIsRead(1);
        this.updateById(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<BlogNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogNotification::getUserId, userId)
               .eq(BlogNotification::getIsRead, 0);
        BlogNotification update = new BlogNotification();
        update.setIsRead(1);
        this.update(update, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long notificationId, Long userId) {
        BlogNotification notification = this.getById(notificationId);
        if (notification == null) {
            throw new BusinessException(404, "通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除");
        }
        this.removeById(notificationId);
    }

    private NotificationVO convertToVO(BlogNotification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setType(notification.getType());
        vo.setTitle(notification.getTitle());
        vo.setContent(notification.getContent());
        vo.setTargetType(notification.getTargetType());
        vo.setTargetId(notification.getTargetId());
        vo.setIsRead(notification.getIsRead() == 1);
        vo.setCreateTime(notification.getCreateTime());
        vo.setTimeAgo(getTimeAgo(notification.getCreateTime()));

        // 获取触发者信息
        if (notification.getFromUserId() != null) {
            SysUser fromUser = sysUserMapper.selectById(notification.getFromUserId());
            if (fromUser != null) {
                UserVO fromUserVO = new UserVO();
                fromUserVO.setId(fromUser.getId());
                fromUserVO.setUsername(fromUser.getUsername());
                fromUserVO.setNickname(fromUser.getNickname());
                fromUserVO.setAvatar(fromUser.getAvatar());
                vo.setFromUser(fromUserVO);
            }
        }

        return vo;
    }

    private String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        long minutes = ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());
        if (minutes < 1) return "刚刚";
        if (minutes < 60) return minutes + "分钟前";
        long hours = minutes / 60;
        if (hours < 24) return hours + "小时前";
        long days = hours / 24;
        if (days < 7) return days + "天前";
        return dateTime.toLocalDate().toString();
    }
}
```

### 3.6 Controller - NotificationController.java

```java
package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.NotificationService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "通知管理", description = "站内通知相关接口")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping("/list")
    public Result<IPage<NotificationVO>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long userId = getCurrentUserId();
        return Result.success(notificationService.getUserNotifications(userId, page, pageSize));
    }

    @Operation(summary = "获取未读数量")
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        Long userId = getCurrentUserId();
        return Result.success(notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(
            @Parameter(description = "通知ID")
            @PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(
            @Parameter(description = "通知ID")
            @PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.deleteNotification(id, userId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
```

---

## 四、热门/趋势系统

### 4.1 数据库设计 - 文章热度表

```sql
-- 文章热度表（用于热门推荐）
CREATE TABLE `blog_trending` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` BIGINT NOT NULL COMMENT '文章ID',
  `score` DOUBLE NOT NULL DEFAULT 0 COMMENT '热度分数',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '当日阅读数',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '当日点赞数',
  `comment_count` INT NOT NULL DEFAULT 0 COMMENT '当日评论数',
  `date` DATE NOT NULL COMMENT '统计日期',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_post_date` (`post_id`, `date`),
  INDEX `idx_date` (`date`),
  INDEX `idx_score` (`score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章热度表';
```

### 4.2 实体类 - BlogTrending.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("blog_trending")
public class BlogTrending implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章ID */
    private Long postId;

    /** 热度分数 */
    private Double score;

    /** 当日阅读数 */
    private Integer viewCount;

    /** 当日点赞数 */
    private Integer likeCount;

    /** 当日评论数 */
    private Integer commentCount;

    /** 统计日期 */
    private LocalDate date;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### 4.3 Mapper - BlogTrendingMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogTrending;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlogTrendingMapper extends BaseMapper<BlogTrending> {

    /**
     * 根据文章ID和日期查询热度记录
     */
    BlogTrending selectByPostIdAndDate(@Param("postId") Long postId, @Param("date") LocalDate date);
}
```

#### 4.3.1 BlogTrendingMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.edu_project.mapper.BlogTrendingMapper">

    <select id="selectByPostIdAndDate" resultType="com.example.edu_project.entity.BlogTrending">
        SELECT * FROM blog_trending
        WHERE post_id = #{postId} AND date = #{date}
        LIMIT 1
    </select>

</mapper>
```

### 4.4 Service - TrendingService.java

```java
package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.vo.PostListResponse;

public interface TrendingService {

    /**
     * 获取热门文章列表
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    IPage<PostListResponse> getTrendingPosts(int page, int pageSize);

    /**
     * 获取热门话题/标签
     * @param limit 数量
     * @return 热门标签列表
     */
    List<Map<String, Object>> getHotTags(int limit);

    /**
     * 更新文章热度（每日定时任务调用）
     * @param postId 文章ID
     */
    void updatePostTrending(Long postId);

    /**
     * 计算热度分数
     * 公式: score = view*1 + like*5 + comment*10
     */
    double calculateScore(int viewCount, int likeCount, int commentCount);
}
```

### 4.3 Service 实现 - TrendingServiceImpl.java

```java
package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.entity.BlogPostTag;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.entity.BlogTrending;
import com.example.edu_project.mapper.BlogPostMapper;
import com.example.edu_project.mapper.BlogPostTagMapper;
import com.example.edu_project.mapper.BlogTagMapper;
import com.example.edu_project.mapper.BlogTrendingMapper;
import com.example.edu_project.service.TrendingService;
import com.example.edu_project.vo.PostListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrendingServiceImpl implements TrendingService {

    @Autowired
    private BlogTrendingMapper trendingMapper;

    @Autowired
    private BlogPostMapper postMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Override
    @Transactional(readOnly = true)
    public IPage<PostListResponse> getTrendingPosts(int page, int pageSize) {
        // 获取最近7天内的热门文章
        LocalDate weekAgo = LocalDate.now().minusDays(7);

        LambdaQueryWrapper<BlogTrending> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(BlogTrending::getDate, weekAgo)
               .orderByDesc(BlogTrending::getScore)
               .select(BlogTrending::getPostId);

        List<BlogTrending> trendingList = trendingMapper.selectList(wrapper);

        if (trendingList.isEmpty()) {
            // 没有热度数据，返回最新文章
            Page<BlogPost> pageParam = new Page<>(page, pageSize);
            LambdaQueryWrapper<BlogPost> postWrapper = new LambdaQueryWrapper<>();
            postWrapper.eq(BlogPost::getStatus, 1)
                      .orderByDesc(BlogPost::getCreateTime);
            Page<BlogPost> postPage = postMapper.selectPage(pageParam, postWrapper);

            Page<PostListResponse> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
            result.setRecords(postPage.getRecords().stream()
                    .map(this::convertToPostListResponse)
                    .collect(Collectors.toList()));
            return result;
        }

        // 按热度排序获取文章ID
        List<Long> postIds = trendingList.stream()
                .sorted(Comparator.comparingDouble(BlogTrending::getScore).reversed())
                .map(BlogTrending::getPostId)
                .collect(Collectors.toList());

        // 分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, postIds.size());
        List<Long> pagePostIds = postIds.subList(start, end);

        // 批量查询文章
        List<BlogPost> posts = postMapper.selectBatchIds(pagePostIds);

        Page<PostListResponse> result = new Page<>(page, pageSize, postIds.size());
        result.setRecords(posts.stream()
                .map(this::convertToPostListResponse)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHotTags(int limit) {
        // 统计最近7天被使用的标签
        LocalDate weekAgo = LocalDate.now().minusDays(7);

        // 简单实现：返回使用最多的标签（使用 Page 避免 .last() SQL 注入风险）
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BlogTag::getPostCount);

        List<BlogTag> tags = blogTagMapper.selectPage(
                new Page<>(1, limit), wrapper).getRecords();

        return tags.stream().map(tag -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tag.getId());
            map.put("name", tag.getName());
            map.put("postCount", tag.getPostCount());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public void updatePostTrending(Long postId) {
        BlogPost post = postMapper.selectById(postId);
        if (post == null) return;

        LocalDate today = LocalDate.now();

        BlogTrending trending = trendingMapper.selectByPostIdAndDate(postId, today);
        if (trending == null) {
            trending = new BlogTrending();
            trending.setPostId(postId);
            trending.setDate(today);
            trending.setViewCount(0);
            trending.setLikeCount(0);
            trending.setCommentCount(0);
        }

        // 更新各项计数（这里需要根据实际增量计算）
        // 简化：直接使用文章的当前计数
        trending.setScore(calculateScore(
                post.getViewCount() != null ? post.getViewCount() : 0,
                post.getLikeCount() != null ? post.getLikeCount() : 0,
                post.getCommentCount() != null ? post.getCommentCount() : 0
        ));

        if (trending.getId() == null) {
            trendingMapper.insert(trending);
        } else {
            trendingMapper.updateById(trending);
        }
    }

    /**
     * 定时更新所有文章热度（每天凌晨执行）
     * 注意：定时任务不能有参数，需遍历所有文章调用 updatePostTrending
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void scheduledUpdateAllTrending() {
        LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPost::getStatus, 1);
        List<BlogPost> posts = postMapper.selectList(wrapper);
        for (BlogPost post : posts) {
            updatePostTrending(post.getId());
        }
    }

    @Override
    public double calculateScore(int viewCount, int likeCount, int commentCount) {
        // 热度计算公式
        // view * 1 + like * 5 + comment * 10
        return viewCount * 1.0 + likeCount * 5.0 + commentCount * 10.0;
    }

    private PostListResponse convertToPostListResponse(BlogPost post) {
        PostListResponse response = new PostListResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setSummary(post.getSummary());
        response.setCategory(post.getCategory());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setCreateTime(post.getCreateTime());
        return response;
    }
}
```

---

## 五、搜索增强

### 5.1 高级搜索接口

```java
// 在 BlogPostController 中添加

/**
 * 高级搜索
 */
@Operation(summary = "高级搜索")
@GetMapping("/search/advanced")
public Result<IPage<PostListResponse>> advancedSearch(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long tagId,
        @RequestParam(required = false) String sortBy, // time/view/like
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize) {

    IPage<PostListResponse> result = blogPostService.advancedSearch(
            keyword, category, userId, tagId, sortBy, page, pageSize);
    return Result.success(result);
}

/**
 * 搜索建议（自动补全）
 */
@Operation(summary = "搜索建议")
@GetMapping("/search/suggest")
public Result<List<String>> getSearchSuggestions(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "10") int limit) {
    // 返回标题中包含关键词的建议
    return Result.success(blogPostService.getSearchSuggestions(keyword, limit));
}
```

---

## 六、用户安全增强

### 6.1 修改密码接口

```java
// 在 SysUserController 中添加

@Operation(summary = "修改密码")
@PutMapping("/password")
public Result<Void> changePassword(
        @RequestBody ChangePasswordRequest request) {

    Long userId = SecurityUtils.getCurrentUserIdOrNull();
    if (userId == null) {
        throw new BusinessException(401, "请先登录");
    }

    sysUserService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
    return Result.success();
}

// DTO
@Data
class ChangePasswordRequest {
    @Schema(description = "旧密码")
    @NotBlank
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank
    @Size(min = 8)
    private String newPassword;
}
```

---

## 七、内容增强 - 草稿自动保存

### 7.1 数据库设计 - 草稿表

```sql
-- 文章草稿表
CREATE TABLE `blog_draft` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(200) DEFAULT NULL COMMENT '草稿标题',
  `content` TEXT COMMENT '草稿内容',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '草稿摘要',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '草稿分类',
  `tag_ids` VARCHAR(200) DEFAULT NULL COMMENT '草稿标签ID（逗号分隔）',
  `post_id` BIGINT DEFAULT NULL COMMENT '关联的文章ID（如果是编辑已有文章）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章草稿表';
```

### 7.2 草稿自动保存接口

```java
// 在 BlogPostController 中添加

@Operation(summary = "保存草稿")
@PostMapping("/draft")
public Result<Long> saveDraft(@RequestBody SaveDraftRequest request) {
    Long userId = SecurityUtils.getCurrentUserIdOrNull();
    if (userId == null) {
        throw new BusinessException(401, "请先登录");
    }
    Long draftId = blogPostService.saveDraft(userId, request);
    return Result.success(draftId);
}

@Operation(summary = "获取我的最新草稿")
@GetMapping("/draft/latest")
public Result<SaveDraftRequest> getLatestDraft() {
    Long userId = SecurityUtils.getCurrentUserIdOrNull();
    if (userId == null) {
        throw new BusinessException(401, "请先登录");
    }
    return Result.success(blogPostService.getLatestDraft(userId));
}

@Operation(summary = "删除草稿")
@DeleteMapping("/draft/{draftId}")
public Result<Void> deleteDraft(@PathVariable Long draftId) {
    Long userId = SecurityUtils.getCurrentUserIdOrNull();
    if (userId == null) {
        throw new BusinessException(401, "请先登录");
    }
    blogPostService.deleteDraft(draftId, userId);
    return Result.success();
}
```

---

## 八、举报管理

### 8.1 数据库设计 - 举报表

```sql
-- 内容举报表
CREATE TABLE `blog_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reporter_id` BIGINT NOT NULL COMMENT '举报人ID',
  `reported_user_id` BIGINT DEFAULT NULL COMMENT '被举报用户ID',
  `target_type` VARCHAR(50) NOT NULL COMMENT '举报目标类型：post/comment/user',
  `target_id` BIGINT NOT NULL COMMENT '举报目标ID',
  `reason` VARCHAR(500) NOT NULL COMMENT '举报原因',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态：0=待处理，1=已处理，2=已驳回',
  `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
  `handler_result` VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_reporter_id` (`reporter_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容举报表';
```

### 8.2 举报接口

```java
@Operation(summary = "举报内容")
@PostMapping("/report")
public Result<Void> reportContent(@RequestBody ReportRequest request) {
    Long userId = SecurityUtils.getCurrentUserIdOrNull();
    if (userId == null) {
        throw new BusinessException(401, "请先登录");
    }
    reportService.createReport(userId, request);
    return Result.success();
}

@Operation(summary = "获取待处理举报列表（管理员）")
@GetMapping("/admin/reports/pending")
public Result<IPage<ReportVO>> getPendingReports(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
    // 需要管理员权限
    return Result.success(reportService.getPendingReports(page, pageSize));
}

@Operation(summary = "处理举报（管理员）")
@PutMapping("/admin/reports/{reportId}")
public Result<Void> handleReport(
        @PathVariable Long reportId,
        @RequestBody HandleReportRequest request) {
    // 需要管理员权限
    Long adminId = SecurityUtils.getCurrentUserIdOrNull();
    reportService.handleReport(reportId, adminId, request);
    return Result.success();
}
```

---

## 九、功能优先级与工作量

### 9.1 优先级排序

| 优先级 | 功能 | 工作量 | 说明 |
|--------|------|--------|------|
| **P0** | 关注/粉丝系统 | 6h | 核心社交功能 |
| **P0** | 站内通知 | 5h | 用户留存关键 |
| **P0** | 用户封禁 | 2h | 安全必需 |
| **P1** | 热门/趋势内容 | 4h | 内容发现 |
| **P1** | @提及功能 | 3h | 社交互动 |
| **P1** | 用户搜索 | 2h | 用户发现 |
| **P1** | 修改密码 | 1h | 安全功能 |
| **P2** | 草稿自动保存 | 3h | 编辑体验 |
| **P2** | #话题标签 | 3h | 内容组织 |
| **P2** | 举报管理 | 4h | 内容治理 |
| **P3** | 私信功能 | 8h | 深度社交 |
| **P3** | 无限滚动 | 2h | 体验优化 |
| **P3** | PWA支持 | 4h | 体验优化 |

### 9.2 建议实施顺序

```
第一阶段（社交基础）：
1. 关注/粉丝系统
2. 站内通知

第二阶段（内容发现）：
3. 热门/趋势内容
4. 用户搜索
5. @提及功能

第三阶段（安全保障）：
6. 修改密码
7. 用户封禁
8. 举报管理

第四阶段（体验优化）：
9. 草稿自动保存
10. #话题标签
```

---

## 十、前端集成

### 10.1 新增页面

| 页面 | 路由 | 功能 |
|------|------|------|
| notifications.html | /notifications.html | 通知列表 |
| followers.html | /followers.html | 粉丝列表 |
| following.html | /following.html | 关注列表 |
| trending.html | /trending.html | 热门内容 |
| search.html | /search.html | 搜索结果 |

### 10.2 导航栏新增

```html
<!-- 通知入口 -->
<a class="nav-link" href="notifications.html" id="notificationLink">
  <span class="nav-icon">🔔</span>
  <span class="badge badge-danger" id="notificationBadge" style="display:none">0</span>
</a>
```

### 10.3 API 封装

```javascript
// js/api.js 添加

// 关注
api.follow = {
  follow: (targetUserId) => axiosInstance.post('/follow', { targetUserId }),
  unfollow: (targetUserId) => axiosInstance.delete('/follow/' + targetUserId),
  check: (targetUserId) => axiosInstance.get('/follow/check/' + targetUserId),
  getFollowers: (userId, page, pageSize) => 
    axiosInstance.get('/follow/followers/' + userId, { params: { page, pageSize } }),
  getFollowing: (userId, page, pageSize) => 
    axiosInstance.get('/follow/following/' + userId, { params: { page, pageSize } })
};

// 通知
api.notification = {
  list: (page, pageSize) => axiosInstance.get('/notification/list', { 
    params: { page, pageSize } 
  }),
  unreadCount: () => axiosInstance.get('/notification/unread-count'),
  markAsRead: (id) => axiosInstance.put('/notification/' + id + '/read'),
  markAllAsRead: () => axiosInstance.put('/notification/read-all'),
  delete: (id) => axiosInstance.delete('/notification/' + id)
};

// 趋势
api.trending = {
  posts: (page, pageSize) => axiosInstance.get('/trending/posts', { 
    params: { page, pageSize } 
  }),
  hotTags: (limit) => axiosInstance.get('/trending/hot-tags', { 
    params: { limit } 
  })
};
```

### 10.4 通知检查轮询

```javascript
// 每 30 秒检查一次未读通知
setInterval(async () => {
  try {
    const res = await api.notification.unreadCount();
    const count = res.data;
    const badge = document.getElementById('notificationBadge');
    if (badge) {
      if (count > 0) {
        badge.textContent = count > 99 ? '99+' : count;
        badge.style.display = 'inline-block';
      } else {
        badge.style.display = 'none';
      }
    }
  } catch (e) {
    // ignore
  }
}, 30000);
```

---

## 十一、SQL 执行脚本汇总

```sql
-- 1. 关注关系表
CREATE TABLE `blog_follow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `follower_id` BIGINT NOT NULL,
  `following_id` BIGINT NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_follower_following` (`follower_id`, `following_id`),
  INDEX `idx_following_id` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注关系表';

-- 2. 通知表
CREATE TABLE `blog_notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(50) NOT NULL COMMENT 'follow/like/comment/mention/system',
  `title` VARCHAR(200) NOT NULL,
  `content` VARCHAR(500) DEFAULT NULL,
  `from_user_id` BIGINT DEFAULT NULL,
  `target_type` VARCHAR(50) DEFAULT NULL COMMENT 'post/comment',
  `target_id` BIGINT DEFAULT NULL,
  `is_read` TINYINT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_user_unread` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 3. 文章热度表
CREATE TABLE `blog_trending` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `score` DOUBLE NOT NULL DEFAULT 0,
  `view_count` INT NOT NULL DEFAULT 0,
  `like_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  `date` DATE NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_post_date` (`post_id`, `date`),
  INDEX `idx_score` (`score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章热度表';

-- 4. 草稿表
CREATE TABLE `blog_draft` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(200) DEFAULT NULL,
  `content` TEXT,
  `summary` VARCHAR(500) DEFAULT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `tag_ids` VARCHAR(200) DEFAULT NULL,
  `post_id` BIGINT DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章草稿表';

-- 5. 举报表
CREATE TABLE `blog_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `reporter_id` BIGINT NOT NULL,
  `reported_user_id` BIGINT DEFAULT NULL,
  `target_type` VARCHAR(50) NOT NULL COMMENT 'post/comment/user',
  `target_id` BIGINT NOT NULL,
  `reason` VARCHAR(500) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0=待处理，1=已处理，2=已驳回',
  `handler_id` BIGINT DEFAULT NULL,
  `handler_result` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `handle_time` DATETIME DEFAULT NULL,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_reporter_id` (`reporter_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容举报表';

-- 6. 为 sys_user 表添加粉丝/关注计数字段
ALTER TABLE `sys_user` 
ADD COLUMN `follower_count` INT NOT NULL DEFAULT 0 COMMENT '粉丝数' AFTER `avatar`,
ADD COLUMN `following_count` INT NOT NULL DEFAULT 0 COMMENT '关注数' AFTER `follower_count`;

-- 7. 为 blog_tag 表添加帖子计数字段（如果没有）
ALTER TABLE `blog_tag` 
ADD COLUMN `post_count` INT NOT NULL DEFAULT 0 COMMENT '帖子数量' AFTER `name`;
```

---

## 十二、注意事项

### 12.0 前置条件（实施前必须完成）

> 以下配置必须在实施增强计划之前完成，否则相关功能无法正常工作。

1. **文件上传配置**：在 `application.yml` 中添加 multipart 配置，否则校友圈图片上传会失败：
   ```yaml
   spring:
     servlet:
       multipart:
         enabled: true
         max-file-size: 10MB
         max-request-size: 50MB
   upload:
     path: /var/www/uploads  # 或本地路径如 D:/uploads
     url-prefix: http://localhost/uploads
   ```

2. **无需添加 fastjson2 依赖**：计划书代码已改用 Hutool JSON 工具（`JSONUtil`），无需额外添加 fastjson2 依赖。

3. **数据库字段同步**：执行 ALTER TABLE 之前，确保同步修改对应的 Java 实体类：
   - `sys_user` ADD `follower_count`, `following_count` → `SysUser.java` 添加 `followerCount`, `followingCount` 字段
   - `blog_tag` ADD `post_count` → `BlogTag.java` 添加 `postCount` 字段
   - `blog_post` ADD `cover_url` → `BlogPost.java` 添加 `coverUrl` 字段

### 12.1 性能考虑

1. **通知查询**：使用索引优化，避免全表扫描
2. **热度计算**：使用定时任务而非实时计算
3. **Feed 流**：实现分页或瀑布流，避免一次加载过多

### 12.2 安全考虑

1. **防止刷关注**：限流、同 IP 限制
2. **通知过滤**：敏感词过滤
3. **举报审核**：人工审核机制
4. **SecurityConfig 路径配置**：新模块接口需要在 `SecurityConfig.java` 中添加 permitAll 规则，否则未登录用户无法访问公开接口：
   ```java
   .requestMatchers("/trending/**").permitAll()           // 热门内容浏览
   .requestMatchers("/circle/feed/recommend").permitAll() // 推荐流
   .requestMatchers("/circle/post/*/comment/**").permitAll() // 评论列表
   .requestMatchers("/follow/followers/**").permitAll()  // 粉丝列表
   .requestMatchers("/follow/following/**").permitAll()  // 关注列表
   .requestMatchers(HttpMethod.GET, "/circle/post/**").permitAll() // 动态详情
   ```

### 12.3 扩展性

1. **实时通知**：后期可改用 WebSocket/SSE
2. **推送通知**：后期可集成极光/Firebase
3. **消息队列**：高并发时使用 MQ 解耦

---

## 十三、相关文档

- [媒体上传计划书](./MEDIA_UPLOAD_PLAN.md) - 图片/视频上传功能
- [项目主文档](./campus_blog.md) - 项目整体架构

---

## 十四、校友圈功能（独立社交动态流）

### 14.1 功能概述

校友圈是一个类似微博/Twitter 的独立社交动态流功能，与博客文章系统分离：

| 特性 | 说明 |
|------|------|
| **短动态** | 支持发布短文本动态（不超过280字） |
| **图片动态** | 支持1-9张图片的图片动态 |
| **点赞互动** | 点赞/取消点赞 |
| **评论互动** | 评论（支持二级回复） |
| **转发动态** | 转发他人动态 |
| **关注流** | 查看关注者的动态 |
| **推荐流** | 推荐热门动态 |
| **话题标签** | #话题功能 |
| **@提及** | @用户提及功能 |

---

### 14.2 数据库设计

#### 14.2.1 动态表 `blog_circle_post`

```sql
-- 校友圈动态表
CREATE TABLE `blog_circle_post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '发布者用户ID',
  `content` VARCHAR(500) NOT NULL COMMENT '动态内容（最多500字）',
  `content_type` TINYINT NOT NULL DEFAULT 1 COMMENT '内容类型：1=纯文本，2=图片，3=转发',
  `image_urls` JSON DEFAULT NULL COMMENT '图片URL数组（最多9张）',
  `repost_id` BIGINT DEFAULT NULL COMMENT '转发的动态ID',
  `repost_user_id` BIGINT DEFAULT NULL COMMENT '被转发者用户ID',
  `repost_content` VARCHAR(500) DEFAULT NULL COMMENT '转发时添加的内容',
  `tags` JSON DEFAULT NULL COMMENT '话题标签数组',
  `mentions` JSON DEFAULT NULL COMMENT '@提及的用户ID数组',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '位置信息',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
  `repost_count` INT NOT NULL DEFAULT 0 COMMENT '转发数',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '查看数',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否，1=是',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=正常，0=隐藏，2=删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_like_count` (`like_count` DESC),
  INDEX `idx_repost_id` (`repost_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校友圈动态表';
```

#### 14.2.2 动态点赞表 `blog_circle_like`

```sql
-- 校友圈点赞表
CREATE TABLE `blog_circle_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `post_id` BIGINT NOT NULL COMMENT '动态ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_post` (`user_id`, `post_id`),
  INDEX `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校友圈点赞表';
```

#### 14.2.3 动态评论表 `blog_circle_comment`

```sql
-- 校友圈评论表
CREATE TABLE `blog_circle_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL COMMENT '动态ID',
  `user_id` BIGINT NOT NULL COMMENT '评论者用户ID',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（二级回复）',
  `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '回复给的用户ID',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_post_id` (`post_id`),
  INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校友圈评论表';
```

#### 14.2.4 动态转发表 `blog_circle_repost`

```sql
-- 校友圈转发表
CREATE TABLE `blog_circle_repost` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '转发者用户ID',
  `original_post_id` BIGINT NOT NULL COMMENT '原始动态ID',
  `new_post_id` BIGINT NOT NULL COMMENT '新动态ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_original_post_id` (`original_post_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校友圈转发表';
```

---

### 14.3 实体类

#### 14.3.1 CirclePost.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("blog_circle_post")
public class CirclePost implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布者用户ID */
    private Long userId;

    /** 动态内容（最多500字） */
    private String content;

    /** 内容类型：1=纯文本，2=图片，3=转发 */
    private Integer contentType;

    /** 图片URL数组（最多9张）JSON格式 */
    private String imageUrls;

    /** 转发的动态ID */
    private Long repostId;

    /** 被转发者用户ID */
    private Long repostUserId;

    /** 转发时添加的内容 */
    private String repostContent;

    /** 话题标签数组JSON格式 */
    private String tags;

    /** @提及的用户ID数组JSON格式 */
    private String mentions;

    /** 位置信息 */
    private String location;

    /** 点赞数 */
    private Integer likeCount;

    /** 评论数 */
    private Integer commentCount;

    /** 转发数 */
    private Integer repostCount;

    /** 查看数 */
    private Integer viewCount;

    /** 是否置顶：0=否，1=是 */
    private Integer isTop;

    /** 状态：1=正常，0=隐藏，2=删除 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

---

### 14.4 DTO

#### 14.4.1 CirclePostCreateRequest.java

```java
package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "校友圈动态创建请求")
public class CirclePostCreateRequest {

    @Schema(description = "动态内容", required = true)
    @NotBlank(message = "内容不能为空")
    @Size(max = 500, message = "内容不能超过500字")
    private String content;

    @Schema(description = "图片文件列表（最多9张）")
    private MultipartFile[] images;

    @Schema(description = "转发的动态ID")
    private Long repostId;

    @Schema(description = "位置信息")
    private String location;
}
```

---

### 14.5 VO

#### 14.5.1 CirclePostVO.java

```java
package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "校友圈动态信息")
public class CirclePostVO implements Serializable {

    private Long id;

    @Schema(description = "发布者用户信息")
    private UserVO user;

    @Schema(description = "动态内容")
    private String content;

    @Schema(description = "内容类型：1=纯文本，2=图片，3=转发")
    private Integer contentType;

    @Schema(description = "图片URL列表")
    private List<String> imageUrls;

    @Schema(description = "转发的原始动态信息")
    private CirclePostVO repostPost;

    @Schema(description = "位置信息")
    private String location;

    @Schema(description = "话题标签列表")
    private List<String> tags;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "转发数")
    private Integer repostCount;

    @Schema(description = "查看数")
    private Integer viewCount;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "是否已转发")
    private Boolean isReposted;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "相对时间")
    private String timeAgo;
}
```

#### 14.5.2 CircleCommentVO.java

```java
package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "校友圈评论信息")
public class CircleCommentVO implements Serializable {

    private Long id;

    @Schema(description = "动态ID")
    private Long postId;

    @Schema(description = "评论者用户信息")
    private UserVO user;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "回复给的用户ID")
    private Long replyToUserId;

    @Schema(description = "回复给的用户昵称")
    private String replyToNickname;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "子评论列表（二级回复）")
    private List<CircleCommentVO> replies;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "相对时间")
    private String timeAgo;
}
```

### 14.6 Mapper 定义

#### 14.6.1 CirclePostMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CirclePost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CirclePostMapper extends BaseMapper<CirclePost> {

    @Update("UPDATE blog_circle_post SET view_count = view_count + 1 WHERE id = #{postId}")
    void incrementViewCount(@Param("postId") Long postId);

    @Update("UPDATE blog_circle_post SET like_count = like_count + 1 WHERE id = #{postId}")
    void incrementLikeCount(@Param("postId") Long postId);

    @Update("UPDATE blog_circle_post SET like_count = like_count - 1 WHERE id = #{postId} AND like_count > 0")
    void decrementLikeCount(@Param("postId") Long postId);

    @Update("UPDATE blog_circle_post SET repost_count = repost_count + 1 WHERE id = #{postId}")
    void incrementRepostCount(@Param("postId") Long postId);
}
```

#### 14.6.2 CircleLike.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("blog_circle_like")
public class CircleLike implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 点赞用户ID */
    private Long userId;

    /** 动态ID */
    private Long postId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer isDeleted;
}
```

#### 14.6.3 CircleLikeMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CircleLikeMapper extends BaseMapper<CircleLike> {
}
```

#### 14.6.4 CircleComment.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("blog_circle_comment")
public class CircleComment implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 动态ID */
    private Long postId;

    /** 评论者用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 父评论ID（二级回复） */
    private Long parentId;

    /** 回复给的用户ID */
    private Long replyToUserId;

    /** 点赞数 */
    private Integer likeCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer isDeleted;
}
```

#### 14.6.5 CircleCommentMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CircleCommentMapper extends BaseMapper<CircleComment> {
}
```

#### 14.6.6 CircleRepost.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("blog_circle_repost")
public class CircleRepost implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 转发者用户ID */
    private Long userId;

    /** 原始动态ID */
    private Long originalPostId;

    /** 新动态ID */
    private Long newPostId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer isDeleted;
}
```

#### 14.6.7 CircleRepostMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleRepost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CircleRepostMapper extends BaseMapper<CircleRepost> {
}
```

#### 14.6.8 BlogMedia.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("blog_media")
public class BlogMedia implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 上传用户ID */
    private Long userId;

    /** 原始文件名 */
    private String fileName;

    /** 存储路径 */
    private String filePath;

    /** 访问URL */
    private String fileUrl;

    /** 文件类型：image/video */
    private String fileType;

    /** MIME类型 */
    private String mimeType;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 图片宽度（仅图片） */
    private Integer width;

    /** 图片高度（仅图片） */
    private Integer height;

    /** 视频缩略图URL */
    private String thumbUrl;

    /** 状态：1=正常，0=禁用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
```

#### 14.6.9 BlogMediaMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogMedia;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogMediaMapper extends BaseMapper<BlogMedia> {
}
```

### 14.7 媒体服务

#### 14.7.1 MediaVO.java

```java
package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "媒体资源信息")
public class MediaVO {

    @Schema(description = "媒体ID")
    private Long id;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "缩略图URL")
    private String thumbUrl;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "图片宽度")
    private Integer width;

    @Schema(description = "图片高度")
    private Integer height;
}
```

#### 14.7.2 MediaService.java

```java
package com.example.edu_project.service;

import com.example.edu_project.vo.MediaVO;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    /**
     * 上传文件
     */
    MediaVO uploadFile(MultipartFile file, Long userId);

    /**
     * 删除文件
     */
    void deleteFile(Long mediaId, Long userId);
}
```

#### 14.7.3 MediaServiceImpl.java

```java
package com.example.edu_project.service.impl;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogMedia;
import com.example.edu_project.mapper.BlogMediaMapper;
import com.example.edu_project.service.MediaService;
import com.example.edu_project.vo.MediaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {

    @Value("${upload.path:/var/www/uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:http://localhost/uploads}")
    private String urlPrefix;

    @Autowired
    private BlogMediaMapper blogMediaMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaVO uploadFile(MultipartFile file, Long userId) {
        // 检查文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        // 获取文件信息
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成文件名
        String filename = UUID.randomUUID().toString() + extension;
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = datePath + "/" + filename;
        String fullPath = uploadPath + "/" + relativePath;

        try {
            // 确保目录存在
            Path path = Paths.get(fullPath).getParent();
            if (path != null && !Files.exists(path)) {
                Files.createDirectories(path);
            }

            // 保存文件
            file.transferTo(new File(fullPath));

            // 保存到数据库
            BlogMedia media = new BlogMedia();
            media.setUserId(userId);
            media.setFileName(originalFilename);
            media.setFilePath(fullPath);
            media.setFileUrl(urlPrefix + "/" + relativePath);
            media.setMimeType(file.getContentType());
            media.setFileSize(file.getSize());
            media.setStatus(1);
            blogMediaMapper.insert(media);

            MediaVO vo = new MediaVO();
            vo.setId(media.getId());
            vo.setFileUrl(media.getFileUrl());
            vo.setFileSize(file.getSize());

            return vo;

        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long mediaId, Long userId) {
        BlogMedia media = blogMediaMapper.selectById(mediaId);
        if (media == null) {
            throw new BusinessException(404, "文件不存在");
        }
        if (!media.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除此文件");
        }

        // 删除物理文件
        try {
            Files.deleteIfExists(Paths.get(media.getFilePath()));
        } catch (IOException e) {
            // 忽略删除失败
        }

        // 删除数据库记录
        blogMediaMapper.deleteById(mediaId);
    }
}
```

### 14.9 Service 接口

#### 14.9.1 CircleService.java

```java
package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.dto.CirclePostCreateRequest;
import com.example.edu_project.entity.CirclePost;
import com.example.edu_project.vo.CirclePostVO;

import java.util.List;

public interface CircleService extends IService<CirclePost> {

    /**
     * 发布动态
     */
    Long createPost(CirclePostCreateRequest request, Long userId);

    /**
     * 删除动态
     */
    void deletePost(Long postId, Long userId);

    /**
     * 获取关注流
     */
    IPage<CirclePostVO> getFollowingFeed(Long userId, int page, int pageSize);

    /**
     * 获取推荐流
     */
    IPage<CirclePostVO> getRecommendFeed(int page, int pageSize);

    /**
     * 获取动态详情
     */
    CirclePostVO getPostDetail(Long postId, Long currentUserId);

    /**
     * 点赞/取消点赞
     */
    void toggleLike(Long postId, Long userId);

    /**
     * 检查是否已点赞
     */
    Boolean checkLikeStatus(Long postId, Long userId);

    /**
     * 获取动态评论
     */
    List<CircleCommentVO> getComments(Long postId);

    /**
     * 转发动态
     */
    Long repostPost(Long originalPostId, CirclePostCreateRequest request, Long userId);
}
```

---

### 14.10 Service 实现（核心逻辑）

#### 14.10.1 CircleServiceImpl.java

```java
package com.example.edu_project.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.CirclePostCreateRequest;
import com.example.edu_project.entity.*;
import com.example.edu_project.mapper.*;
import com.example.edu_project.service.CircleService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@Service
public class CircleServiceImpl extends ServiceImpl<CirclePostMapper, CirclePost>
        implements CircleService {

    @Autowired
    private CircleLikeMapper circleLikeMapper;

    @Autowired
    private CircleCommentMapper circleCommentMapper;

    @Autowired
    private CircleRepostMapper circleRepostMapper;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BlogFollowMapper blogFollowMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPost(CirclePostCreateRequest request, Long userId) {
        // 验证内容长度
        if (request.getContent().length() > 500) {
            throw new BusinessException(400, "内容不能超过500字");
        }

        // 处理图片上传
        List<String> imageUrls = null;
        if (request.getImages() != null && request.getImages().length > 0) {
            if (request.getImages().length > 9) {
                throw new BusinessException(400, "最多上传9张图片");
            }
            imageUrls = new ArrayList<>();
            for (MultipartFile file : request.getImages()) {
                MediaVO media = mediaService.uploadFile(file, userId);
                imageUrls.add(media.getFileUrl());
            }
        }

        // 创建动态
        CirclePost post = new CirclePost();
        post.setUserId(userId);
        post.setContent(request.getContent());
        
        // 确定内容类型
        if (request.getImages() != null && request.getImages().length > 0) {
            post.setContentType(2);
            post.setImageUrls(JSONUtil.toJsonStr(imageUrls));
        } else if (request.getRepostId() != null) {
            post.setContentType(3);
            post.setRepostId(request.getRepostId());
            post.setRepostContent(request.getContent());
        } else {
            post.setContentType(1);
        }
        
        post.setLocation(request.getLocation());
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setRepostCount(0);
        post.setViewCount(0);
        post.setStatus(1);
        
        this.save(post);

        return post.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId, Long userId) {
        CirclePost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        if (!post.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此动态");
        }

        // 逻辑删除
        post.setStatus(2);
        this.updateById(post);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<CirclePostVO> getFollowingFeed(Long userId, int page, int pageSize) {
        // 获取关注的用户ID列表
        LambdaQueryWrapper<BlogFollow> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(BlogFollow::getFollowerId, userId);
        List<BlogFollow> follows = blogFollowMapper.selectList(followWrapper);

        if (follows.isEmpty()) {
            // 没有关注任何人，返回空结果
            return new Page<>(page, pageSize, 0);
        }

        // 获取被关注用户的ID列表
        List<Long> followingIds = follows.stream()
                .map(BlogFollow::getFollowingId)
                .collect(Collectors.toList());

        // 查询这些用户的动态
        Page<CirclePost> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<CirclePost> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CirclePost::getUserId, followingIds)
               .eq(CirclePost::getStatus, 1)
               .orderByDesc(CirclePost::getCreateTime);

        Page<CirclePost> postPage = this.page(pageParam, wrapper);

        // 转换为 VO
        Page<CirclePostVO> voPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        voPage.setRecords(postPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<CirclePostVO> getRecommendFeed(int page, int pageSize) {
        // 获取推荐动态
        Page<CirclePost> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<CirclePost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CirclePost::getStatus, 1)
               .orderByDesc(CirclePost::getCreateTime);
        
        Page<CirclePost> postPage = this.page(pageParam, wrapper);
        
        // 转换为 VO
        Page<CirclePostVO> voPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        voPage.setRecords(postPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        
        return voPage;
    }

    @Override
    @Transactional(readOnly = true)
    public CirclePostVO getPostDetail(Long postId, Long currentUserId) {
        CirclePost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        if (post.getStatus() != 1) {
            throw new BusinessException(404, "动态不存在");
        }

        // 增加查看数
        this.baseMapper.incrementViewCount(postId);

        CirclePostVO vo = convertToVO(post);
        
        // 检查点赞和转发状态
        if (currentUserId != null) {
            vo.setIsLiked(checkLikeStatus(postId, currentUserId));
            vo.setIsRepost(checkRepostStatus(postId, currentUserId));
        }
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Long postId, Long userId) {
        CirclePost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }

        // 检查是否已点赞
        LambdaQueryWrapper<CircleLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleLike::getUserId, userId)
               .eq(CircleLike::getPostId, postId);

        if (this.count(wrapper) > 0) {
            // 取消点赞
            this.remove(wrapper);
            this.baseMapper.decrementLikeCount(postId);
        } else {
            // 点赞
            CircleLike likeRecord = new CircleLike();
            likeRecord.setUserId(userId);
            likeRecord.setPostId(postId);
            this.save(likeRecord);
            this.baseMapper.incrementLikeCount(postId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkLikeStatus(Long postId, Long userId) {
        if (userId == null) return false;
        LambdaQueryWrapper<CircleLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleLike::getUserId, userId)
               .eq(CircleLike::getPostId, postId);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CircleCommentVO> getComments(Long postId) {
        LambdaQueryWrapper<CircleComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleComment::getPostId, postId)
               .isNull(CircleComment::getParentId)  // 只获取一级评论
               .orderByDesc(CircleComment::getCreateTime);

        List<CircleComment> comments = circleCommentMapper.selectList(wrapper);

        return comments.stream()
                .map(this::convertToCommentVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long repostPost(Long originalPostId, CirclePostCreateRequest request, Long userId) {
        CirclePost originalPost = this.getById(originalPostId);
        if (originalPost == null) {
            throw new BusinessException(404, "原动态不存在");
        }

        // 创建新动态
        CirclePost newPost = new CirclePost();
        newPost.setUserId(userId);
        newPost.setContent(request.getContent());
        newPost.setContentType(3);
        newPost.setRepostId(originalPostId);
        newPost.setRepostUserId(originalPost.getUserId());
        newPost.setRepostContent(request.getContent());
        newPost.setStatus(1);
        this.save(newPost);

        // 增加转发计数
        this.baseMapper.incrementRepostCount(originalPostId);

        return newPost.getId();
    }

    private CirclePostVO convertToVO(CirclePost post) {
        CirclePostVO vo = new CirclePostVO();
        vo.setId(post.getId());
        vo.setContent(post.getContent());
        vo.setContentType(post.getContentType());
        
        // 解析图片
        if (post.getImageUrls() != null) {
            vo.setImageUrls(JSONUtil.toList(post.getImageUrls(), String.class));
        }
        
        vo.setLocation(post.getLocation());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setRepostCount(post.getRepostCount());
        vo.setViewCount(post.getViewCount());
        vo.setIsTop(post.getIsTop() == 1);
        vo.setCreateTime(post.getCreateTime());
        vo.setTimeAgo(getTimeAgo(post.getCreateTime()));

        // 获取用户信息
        SysUser user = sysUserMapper.selectById(post.getUserId());
        if (user != null) {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setNickname(user.getNickname());
            userVO.setAvatar(user.getAvatar());
            vo.setUser(userVO);
        }

        // 解析话题标签
        if (post.getTags() != null) {
            vo.setTags(JSONUtil.toList(post.getTags(), String.class));
        }

        return vo;
    }

    private CircleCommentVO convertToCommentVO(CircleComment comment) {
        CircleCommentVO vo = new CircleCommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount());
        vo.setCreateTime(comment.getCreateTime());
        vo.setTimeAgo(getTimeAgo(comment.getCreateTime()));

        // 获取用户信息
        SysUser user = sysUserMapper.selectById(comment.getUserId());
        if (user != null) {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setNickname(user.getNickname());
            userVO.setAvatar(user.getAvatar());
            vo.setUser(userVO);
        }

        // 获取回复信息
        if (comment.getReplyToUserId() != null) {
            vo.setReplyToUserId(comment.getReplyToUserId());
            SysUser replyUser = sysUserMapper.selectById(comment.getReplyToUserId());
            if (replyUser != null) {
                vo.setReplyToNickname(replyUser.getNickname());
            }
        }

        return vo;
    }

    private String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        long minutes = ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());
        if (minutes < 1) return "刚刚";
        if (minutes < 60) return minutes + "分钟前";
        long hours = minutes / 60;
        if (hours < 24) return hours + "小时前";
        long days = hours / 24;
        if (days < 7) return days + "天前";
        return dateTime.toLocalDate().toString();
    }

    private Boolean checkRepostStatus(Long postId, Long userId) {
        if (userId == null) return false;
        LambdaQueryWrapper<CircleRepost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleRepost::getUserId, userId)
               .eq(CircleRepost::getOriginalPostId, postId);
        return circleRepostMapper.selectCount(wrapper) > 0;
    }
}
```

---

### 14.11 Controller

#### 14.11.1 CircleController.java

```java
package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.CirclePostCreateRequest;
import com.example.edu_project.service.CircleService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.CirclePostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/circle")
@Tag(name = "校友圈", description = "校友圈动态流相关接口")
public class CircleController {

    @Autowired
    private CircleService circleService;

    @Autowired
    private MediaService mediaService;

    @Operation(summary = "发布动态")
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Long> createPost(
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestParam(required = false) Long repostId,
            @RequestParam(required = false) String location) {

        Long userId = getCurrentUserId();

        CirclePostCreateRequest request = new CirclePostCreateRequest();
        request.setContent(content);
        request.setImages(images);
        request.setRepostId(repostId);
        request.setLocation(location);

        Long postId = circleService.createPost(request, userId);
        return Result.success(postId);
    }

    @Operation(summary = "删除动态")
    @DeleteMapping("/post/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId) {
        Long userId = getCurrentUserId();
        circleService.deletePost(postId, userId);
        return Result.success();
    }

    @Operation(summary = "获取关注流")
    @GetMapping("/feed/following")
    public Result<IPage<CirclePostVO>> getFollowingFeed(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long userId = getCurrentUserId();
        return Result.success(circleService.getFollowingFeed(userId, page, pageSize));
    }

    @Operation(summary = "获取推荐流")
    @GetMapping("/feed/recommend")
    public Result<IPage<CirclePostVO>> getRecommendFeed(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(circleService.getRecommendFeed(page, pageSize));
    }

    @Operation(summary = "获取动态详情")
    @GetMapping("/post/{postId}")
    public Result<CirclePostVO> getPostDetail(@PathVariable Long postId) {
        Long userId = getCurrentUserIdOrNull();
        return Result.success(circleService.getPostDetail(postId, userId));
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/like/{postId}")
    public Result<Void> toggleLike(@PathVariable Long postId) {
        Long userId = getCurrentUserId();
        circleService.toggleLike(postId, userId);
        return Result.success();
    }

    @Operation(summary = "转发动态")
    @PostMapping("/repost/{postId}")
    public Result<Long> repostPost(
            @PathVariable Long postId,
            @RequestParam String content) {

        Long userId = getCurrentUserId();

        CirclePostCreateRequest request = new CirclePostCreateRequest();
        request.setContent(content);
        request.setRepostId(postId);

        Long newPostId = circleService.repostPost(postId, request, userId);
        return Result.success(newPostId);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/media/{mediaId}")
    public Result<Void> deleteMedia(@PathVariable Long mediaId) {
        Long userId = getCurrentUserId();
        mediaService.deleteFile(mediaId, userId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }

    private Long getCurrentUserIdOrNull() {
        return SecurityUtils.getCurrentUserIdOrNull();
    }
}
```

---

### 14.12 前端页面

#### 14.12.1 circle.html - 校友圈主页

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>校友圈 - 校园博客论坛</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="css/style.css" rel="stylesheet">
</head>
<body>
  <nav class="navbar navbar-expand-lg sticky-top">
    <div class="container">
      <a class="navbar-brand" href="index.html">
        <span class="nav-icon">📚</span> 校园博客
      </a>
      <div class="navbar-nav ms-auto">
        <a class="nav-link active" href="circle.html">
          <span class="nav-icon">🌐</span> 校友圈
        </a>
        <a class="nav-link" href="index.html">
          <span class="nav-icon">📖</span> 文章
        </a>
        <a class="nav-link" href="profile.html" id="profileLink">
          <span class="nav-icon">👤</span> 个人中心
        </a>
        <button class="theme-toggle" id="themeToggle" title="切换主题">🌙</button>
      </div>
    </div>
  </nav>

  <div class="container mt-4">
    <div class="row">
      <!-- 左侧：发布动态 -->
      <div class="col-lg-4 mb-3">
        <div class="card fade-in-up">
          <div class="card-body">
            <textarea class="form-control mb-2" 
                      id="circleContent" 
                      rows="3" 
                      placeholder="分享你的想法..." 
                      maxlength="500"></textarea>
            <div class="d-flex justify-content-between align-items-center">
              <span class="text-muted small" id="charCount">0/500</span>
              <button type="button" class="btn btn-primary" id="publishBtn" onclick="publishCircle()">
                发布
              </button>
            </div>
            <div class="mt-2">
              <button type="button" class="btn btn-outline-primary btn-sm me-1" onclick="document.getElementById('imageInput').click()">
                📷 图片
              </button>
              <button type="button" class="btn btn-outline-secondary btn-sm" onclick="document.getElementById('locationInput').click()">
                📍 位置
              </button>
              <input type="file" id="imageInput" accept="image/*" multiple style="display:none" onchange="handleImageSelect(this.files)">
              <input type="text" id="locationInput" placeholder="添加位置" style="display:none" onblur="this.style.display='none'">
            </div>
            <!-- 图片预览 -->
            <div id="imagePreview" class="d-flex flex-wrap gap-2 mt-2"></div>
          </div>
        </div>

        <!-- 话题标签 -->
        <div class="card mt-3 fade-in-up" style="animation-delay: 0.1s">
          <div class="card-body">
            <h6 class="mb-3">🏷️ 热门话题</h6>
            <div id="hotTags" class="d-flex flex-wrap gap-2">
              <span class="badge rounded-pill bg-light text-dark"># 校园生活</span>
              <span class="badge rounded-pill bg-light text-dark"># 学习分享</span>
              <span class="badge rounded-pill bg-light text-dark"># 活动通知</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：动态流 -->
      <div class="col-lg-8">
        <ul class="nav nav-pills mb-3 fade-in-up" id="feedTabs">
          <li class="nav-item">
            <a class="nav-link active" href="#" onclick="loadRecommendFeed(event)" data-tab="recommend">推荐</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#" onclick="loadFollowingFeed(event)" data-tab="following">关注</a>
          </li>
        </ul>

        <div id="circleFeed">
          <div class="loading"></div>
        </div>

        <!-- 加载更多 -->
        <div id="loadMore" class="text-center mt-3 d-none">
          <button class="btn btn-outline-secondary" onclick="loadMore()">加载更多</button>
        </div>
      </div>
    </div>
  </div>

  <script src="js/api.js"></script>
  <script src="js/utils.js"></script>
  </body>
</html>
```

---

### 14.13 API 封装

```javascript
// js/api.js 添加

// 校友圈
api.circle = {
  publish: (data) => axiosInstance.post('/circle/post', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  delete: (postId) => axiosInstance.delete('/circle/post/' + postId),
  getFollowingFeed: (page, pageSize) => 
    axiosInstance.get('/circle/feed/following', { params: { page, pageSize } }),
  getRecommendFeed: (page, pageSize) => 
    axiosInstance.get('/circle/feed/recommend', { params: { page, pageSize } }),
  getDetail: (postId) => axiosInstance.get('/circle/post/' + postId),
  toggleLike: (postId) => axiosInstance.post('/circle/like/' + postId),
  repost: (postId, content) => axiosInstance.post('/circle/repost/' + postId, { content })
};
```

---

## 十五、完整SQL执行脚本（包含校友圈）

```sql
-- ==================== 社交系统 ====================

-- 关注关系表
CREATE TABLE `blog_follow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `follower_id` BIGINT NOT NULL,
  `following_id` BIGINT NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_follower_following` (`follower_id`, `following_id`),
  INDEX `idx_following_id` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注关系表';

-- ==================== 通知系统 ====================

CREATE TABLE `blog_notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(50) NOT NULL COMMENT 'follow/like/comment/mention/system',
  `title` VARCHAR(200) NOT NULL,
  `content` VARCHAR(500) DEFAULT NULL,
  `from_user_id` BIGINT DEFAULT NULL,
  `target_type` VARCHAR(50) DEFAULT NULL COMMENT 'post/comment',
  `target_id` BIGINT DEFAULT NULL,
  `is_read` TINYINT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_user_unread` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ==================== 趋势系统 ====================

CREATE TABLE `blog_trending` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `score` DOUBLE NOT NULL DEFAULT 0,
  `view_count` INT NOT NULL DEFAULT 0,
  `like_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  `date` DATE NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_post_date` (`post_id`, `date`),
  INDEX `idx_score` (`score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章热度表';

-- ==================== 内容增强 ====================

CREATE TABLE `blog_draft` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(200) DEFAULT NULL,
  `content` TEXT,
  `summary` VARCHAR(500) DEFAULT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `tag_ids` VARCHAR(200) DEFAULT NULL,
  `post_id` BIGINT DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章草稿表';

-- ==================== 举报管理 ====================

CREATE TABLE `blog_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `reporter_id` BIGINT NOT NULL,
  `reported_user_id` BIGINT DEFAULT NULL,
  `target_type` VARCHAR(50) NOT NULL COMMENT 'post/comment/user',
  `target_id` BIGINT NOT NULL,
  `reason` VARCHAR(500) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0=待处理，1=已处理，2=已驳回',
  `handler_id` BIGINT DEFAULT NULL,
  `handler_result` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `handle_time` DATETIME DEFAULT NULL,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_reporter_id` (`reporter_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容举报表';

-- ==================== 校友圈系统 ====================

-- 校友圈动态表
CREATE TABLE `blog_circle_post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `content` VARCHAR(500) NOT NULL COMMENT '动态内容（最多500字）',
  `content_type` TINYINT NOT NULL DEFAULT 1 COMMENT '内容类型：1=纯文本，2=图片，3=转发',
  `image_urls` JSON DEFAULT NULL COMMENT '图片URL数组',
  `repost_id` BIGINT DEFAULT NULL COMMENT '转发的动态ID',
  `repost_user_id` BIGINT DEFAULT NULL COMMENT '被转发者用户ID',
  `repost_content` VARCHAR(500) DEFAULT NULL COMMENT '转发时添加的内容',
  `tags` JSON DEFAULT NULL COMMENT '话题标签数组',
  `mentions` JSON DEFAULT NULL COMMENT '@提及的用户ID数组',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '位置信息',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
  `repost_count` INT NOT NULL DEFAULT 0 COMMENT '转发数',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '查看数',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否，1=是',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=正常，0=隐藏，2=删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_like_count` (`like_count` DESC),
  INDEX `idx_repost_id` (`repost_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校友圈动态表';

-- 校友圈点赞表
CREATE TABLE `blog_circle_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `post_id` BIGINT NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_post` (`user_id`, `post_id`),
  INDEX `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校友圈点赞表';

-- 校友圈评论表
CREATE TABLE `blog_circle_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL COMMENT '动态ID',
  `user_id` BIGINT NOT NULL COMMENT '评论者用户ID',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（二级回复）',
  `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '回复给的用户ID',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_post_id` (`post_id`),
  INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校友圈评论表';

-- 校友圈转发表
CREATE TABLE `blog_circle_repost` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '转发者用户ID',
  `original_post_id` BIGINT NOT NULL COMMENT '原始动态ID',
  `new_post_id` BIGINT NOT NULL COMMENT '新动态ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=删除',
  PRIMARY KEY (`id`),
  INDEX `idx_original_post_id` (`original_post_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校友圈转发表';

-- ==================== 用户表扩展 ====================

ALTER TABLE `sys_user` 
ADD COLUMN `follower_count` INT NOT NULL DEFAULT 0 COMMENT '粉丝数' AFTER `avatar`,
ADD COLUMN `following_count` INT NOT NULL DEFAULT 0 COMMENT '关注数' AFTER `follower_count`;

-- ==================== 标签表扩展 ====================

ALTER TABLE `blog_tag` 
ADD COLUMN `post_count` INT NOT NULL DEFAULT 0 COMMENT '帖子数量' AFTER `name`;

-- ==================== 媒体表 ====================

CREATE TABLE `blog_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '上传用户ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
  `file_url` VARCHAR(500) NOT NULL COMMENT '访问URL',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型：image/video',
  `mime_type` VARCHAR(100) NOT NULL COMMENT 'MIME类型',
  `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
  `width` INT DEFAULT NULL COMMENT '图片宽度（仅图片）',
  `height` INT DEFAULT NULL COMMENT '图片高度（仅图片）',
  `thumb_url` VARCHAR(500) DEFAULT NULL COMMENT '视频缩略图URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=正常，0=禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_file_type` (`file_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体资源表';

-- ==================== 文章媒体关联表 ====================

CREATE TABLE `blog_post_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` BIGINT NOT NULL COMMENT '文章ID',
  `media_id` BIGINT NOT NULL COMMENT '媒体ID',
  `display_order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_post_id` (`post_id`),
  INDEX `idx_media_id` (`media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章媒体关联表';

-- ==================== 文章扩展 ====================

ALTER TABLE `blog_post` 
ADD COLUMN `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL' AFTER `summary`;
```

---

## 十六、功能优先级与工作量（更新）

| 优先级 | 功能 | 模块 | 工作量 | 说明 |
|--------|------|------|--------|------|
| **P0** | 关注/粉丝系统 | 社交 | 6h | 核心社交功能 |
| **P0** | 站内通知 | 通知 | 5h | 用户留存关键 |
| **P0** | 校友圈基础 | 校友圈 | 8h | 独立社交动态流 |
| **P0** | 媒体上传 | 媒体 | 8h | 图片/视频上传 |
| **P1** | 热门/趋势内容 | 趋势 | 4h | 内容发现 |
| **P1** | @提及功能 | 社交 | 3h | 社交互动 |
| **P1** | 用户搜索 | 搜索 | 2h | 用户发现 |
| **P1** | 修改密码 | 用户 | 1h | 安全功能 |
| **P2** | 草稿自动保存 | 内容 | 3h | 编辑体验 |
| **P2** | #话题标签 | 内容 | 3h | 内容组织 |
| **P2** | 举报管理 | 治理 | 4h | 内容治理 |
| **P3** | 私信功能 | 社交 | 8h | 深度社交 |
| **P3** | 无限滚动 | 体验 | 2h | 体验优化 |

---

**文档版本**：v1.17
**更新日期**：2026-04-25
**包含模块**：社交系统、通知系统、趋势系统、搜索增强、用户安全、内容增强、举报管理、媒体上传、校友圈
**新增表格**：10 张
**新增预计工作量**：约 70-75 小时

### 更新记录
- v3.3: 验证修复版 - 修复严重阻塞和设计遗漏问题
  - 替换 fastjson2 为 Hutool JSONUtil（无需额外依赖）
  - 修复 BlogTrendingMapper.xml 引用不存在的 is_deleted 列
  - 统一热度表名为 blog_trending（第860行 blog_post_trending → blog_trending）
  - 为 blog_follow, blog_notification, blog_circle_like, blog_circle_comment, blog_circle_repost, blog_draft, blog_report 的表定义和实体类添加 is_deleted 字段和 @TableLogic 注解（与项目全局逻辑删除策略一致）
  - 修复完整SQL脚本（第十五章）中 blog_notification、blog_draft、blog_report、blog_circle_repost 缺少 is_deleted 的问题
  - 修复 FollowServiceImpl 并发安全问题（添加 DuplicateKeyException 处理）
  - 修复 TrendingServiceImpl.getHotTags() SQL 注入风险（.last("LIMIT") → Page）
  - 修复 TrendingServiceImpl.updatePostTrending() 方法签名（定时任务不能有参数，拆分为 scheduledUpdateAllTrending）
  - 为所有只读方法添加 @Transactional(readOnly = true)
  - 添加 12.0 前置条件章节（文件上传配置、数据库字段同步要求）
  - 添加 SecurityConfig 路径配置说明
- v3.2: 修复 CircleController 重复代码和注解
  - 删除重复的 @Autowired 和方法定义
  - 添加缺失的 @Operation 注解
  - 修复章节编号问题
- v3.1: 修复缺失的实体类、Mapper 和方法定义
  - 添加 BlogTrending 实体类和 Mapper
  - 添加 BlogTrendingMapper.xml 自定义 SQL
  - 添加 BlogTagMapper 自动注入
  - 添加 CircleLike/CircleComment/CircleRepost 实体类和 Mapper
  - 添加 BlogMedia 实体类和 Mapper
  - 添加 MediaService 实现类
  - 添加 MediaVO、CircleCommentVO
  - 添加 CircleServiceImpl 缺失的导入和方法
  - 添加 BlogFollowMapper 注入
  - 实现 getFollowingFeed 方法
  - 添加 CirclePostMapper @Update 注解
  - 修复 CircleController 的导入和注解
