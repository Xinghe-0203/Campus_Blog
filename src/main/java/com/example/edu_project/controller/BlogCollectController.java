package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.BlogCollectService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.CollectItemVO;
import com.example.edu_project.vo.CollectResultVO;
import com.example.edu_project.vo.CollectStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 */
@Tag(name = "收藏管理", description = "收藏相关接口")
@RestController
@RequestMapping("/collect")
public class BlogCollectController {

    @Autowired
    private BlogCollectService blogCollectService;

    /**
     * 收藏/取消收藏
     */
    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/{postId}")
    public Result<CollectResultVO> toggleCollect(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        CollectResultVO result = blogCollectService.toggleCollect(postId, userId);
        return Result.success(result);
    }

    /**
     * 检查是否已收藏
     */
    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{postId}")
    public Result<CollectStatusVO> checkCollectStatus(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        CollectStatusVO status = blogCollectService.checkCollectStatus(postId, userId);
        return Result.success(status);
    }

    /**
     * 获取我的收藏列表
     */
    @Operation(summary = "获取我的收藏列表")
    @GetMapping("/my")
    public Result<IPage<CollectItemVO>> getMyCollections(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        IPage<CollectItemVO> result = blogCollectService.getMyCollections(userId, page, pageSize);
        return Result.success(result);
    }
}
