package com.example.edu_project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.NotificationService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@Tag(name = "通知管理", description = "通知相关接口")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取通知列表（分页）
     */
    @Operation(summary = "获取通知列表")
    @GetMapping("/list")
    public Result<Page<NotificationVO>> getNotificationList(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Page<NotificationVO> page = notificationService.getNotificationList(pageNum, pageSize, userId);
        return Result.success(page);
    }

    /**
     * 获取未读通知数量
     */
    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记单条通知为已读
     */
    @Operation(summary = "标记单条通知为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        notificationService.markAsRead(id, userId);
        return Result.success(null);
    }

    /**
     * 标记所有通知为已读
     */
    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        notificationService.markAllAsRead(userId);
        return Result.success(null);
    }

    /**
     * 删除通知
     */
    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        notificationService.deleteNotification(id, userId);
        return Result.success(null);
    }
}
