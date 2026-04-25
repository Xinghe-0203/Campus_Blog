# 校园博客论坛系统 - 功能增强计划书

> 校园博客论坛系统 - 功能增强模块
> 版本：v2.0
> 日期：2026-04-25
> 状态：📋 规划中

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
        this.save(follow);
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
    public boolean isFollowing(Long followerId, Long followingId) {
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowerId, followerId)
               .eq(BlogFollow::getFollowingId, followingId);
        return this.count(wrapper) > 0;
    }

    @Override
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
    public int getFollowerCount(Long userId) {
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowingId, userId);
        return (int) this.count(wrapper);
    }

    @Override
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
CREATE TABLE `blog_post_trending` (
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

### 4.2 Service - TrendingService.java

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

    @Override
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

            return convertToResponsePage(postPage);
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
    public List<Map<String, Object>> getHotTags(int limit) {
        // 统计最近7天被使用的标签
        LocalDate weekAgo = LocalDate.now().minusDays(7);

        // 简单实现：返回使用最多的标签
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BlogTag::getPostCount)
               .last("LIMIT " + limit);

        List<BlogTag> tags = blogTagMapper.selectList(wrapper);

        return tags.stream().map(tag -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tag.getId());
            map.put("name", tag.getName());
            map.put("postCount", tag.getPostCount());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
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

### 12.1 性能考虑

1. **通知查询**：使用索引优化，避免全表扫描
2. **热度计算**：使用定时任务而非实时计算
3. **Feed 流**：实现分页或瀑布流，避免一次加载过多

### 12.2 安全考虑

1. **防止刷关注**：限流、同 IP 限制
2. **通知过滤**：敏感词过滤
3. **举报审核**：人工审核机制

### 12.3 扩展性

1. **实时通知**：后期可改用 WebSocket/SSE
2. **推送通知**：后期可集成极光/Firebase
3. **消息队列**：高并发时使用 MQ 解耦

---

## 十三、相关文档

- [媒体上传计划书](./MEDIA_UPLOAD_PLAN.md) - 图片/视频上传功能
- [项目主文档](./campus_blog.md) - 项目整体架构

---

**文档版本**：v2.0
**更新日期**：2026-04-25
**包含模块**：社交系统、通知系统、趋势系统、搜索增强、用户安全、内容增强、举报管理
**总预计工作量**：约 45-50 小时
