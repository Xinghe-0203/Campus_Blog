package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.BlogLikeService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.LikeResultVO;
import com.example.edu_project.vo.LikeStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 */
@Tag(name = "点赞管理", description = "点赞相关接口")
@RestController
@RequestMapping("/like")
@CrossOrigin
public class BlogLikeController {

    @Autowired
    private BlogLikeService blogLikeService;

    /**
     * 点赞/取消点赞
     */
    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/{postId}")
    public Result<LikeResultVO> toggleLike(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        LikeResultVO result = blogLikeService.toggleLike(postId, userId);
        return Result.success(result);
    }

    /**
     * 检查是否已点赞
     */
    @Operation(summary = "检查是否已点赞")
    @GetMapping("/check/{postId}")
    public Result<LikeStatusVO> checkLikeStatus(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        LikeStatusVO status = blogLikeService.checkLikeStatus(postId, userId);
        return Result.success(status);
    }
}
