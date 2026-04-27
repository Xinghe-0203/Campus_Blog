package com.example.edu_project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.SendMessageRequest;
import com.example.edu_project.service.MessageService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 私信控制器
 */
@Tag(name = "私信管理", description = "私信相关接口")
@RestController
@RequestMapping("/message")
@Validated
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送私信
     */
    @Operation(summary = "发送私信")
    @PostMapping("/send")
    public Result<MessageVO> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        Long senderId = SecurityUtils.getCurrentUserIdOrNull();
        if (senderId == null) {
            throw new BusinessException(401, "请先登录");
        }

        com.example.edu_project.entity.Message message = messageService.sendMessage(
            senderId,
            request.getReceiverId(),
            request.getContent()
        );

        // 转换为VO
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setContent(message.getContent());
        vo.setIsRead(message.getIsRead());
        vo.setCreateTime(message.getCreateTime());

        return Result.success(vo);
    }

    /**
     * 获取收到的私信列表（分页）
     */
    @Operation(summary = "获取收到的私信列表")
    @GetMapping("/received")
    public Result<Page<MessageVO>> getReceivedMessages(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Page<MessageVO> pageResult = messageService.getReceivedMessages(page, size, userId);
        return Result.success(pageResult);
    }

    /**
     * 获取发送的私信列表（分页）
     */
    @Operation(summary = "获取发送的私信列表")
    @GetMapping("/sent")
    public Result<Page<MessageVO>> getSentMessages(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Page<MessageVO> pageResult = messageService.getSentMessages(page, size, userId);
        return Result.success(pageResult);
    }

    /**
     * 标记私信为已读
     */
    @Operation(summary = "标记私信为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        messageService.markAsRead(id, userId);
        return Result.success(null);
    }

    /**
     * 删除私信（软删除）
     */
    @Operation(summary = "删除私信")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        messageService.deleteMessage(id, userId);
        return Result.success(null);
    }

    /**
     * 获取未读私信数量
     */
    @Operation(summary = "获取未读私信数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Long count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }
}