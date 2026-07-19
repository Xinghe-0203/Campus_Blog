package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.CircleCommentRequest;
import com.example.edu_project.dto.CirclePostCreateRequest;
import com.example.edu_project.service.CircleService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.CircleCommentVO;
import com.example.edu_project.vo.CirclePostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 校友圈控制器
 */
@Slf4j
@Tag(name = "校友圈", description = "校友圈动态相关接口")
@RestController
@RequestMapping("/circle")
@Validated
public class CircleController {

    @Autowired
    private CircleService circleService;

    /**
     * 发布动态
     */
    @Operation(summary = "发布动态")
    @PostMapping("/post")
    public Result<Long> createPost(@Valid @RequestBody CirclePostCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Long postId = circleService.createPost(
                request.getContent(),
                request.getImageUrls(),
                request.getLocation(),
                request.getRepostId(),
                request.getTags(),
                userId,
                request.getVisibility(),
                request.getAllowComment(),
                request.getAllowRepost()
        );

        return Result.success(postId);
    }

    /**
     * 删除动态
     */
    @Operation(summary = "删除动态")
    @DeleteMapping("/post/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        circleService.deletePost(postId, userId);
        return Result.success(null);
    }

    /**
     * 获取推荐流
     */
    @Operation(summary = "获取推荐流")
    @GetMapping("/feed/recommend")
    public Result<List<CirclePostVO>> getRecommendFeed(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        List<CirclePostVO> feed = circleService.getRecommendFeed(page, pageSize, userId);
        return Result.success(feed);
    }

    /**
     * 获取关注流
     */
    @Operation(summary = "获取关注流")
    @GetMapping("/feed/following")
    public Result<List<CirclePostVO>> getFollowingFeed(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        List<CirclePostVO> feed = circleService.getFollowingFeed(page, pageSize, userId);
        return Result.success(feed);
    }

    /**
     * 获取动态详情
     */
    @Operation(summary = "获取动态详情")
    @GetMapping("/post/{postId}")
    public Result<CirclePostVO> getPostDetail(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        CirclePostVO detail = circleService.getPostDetail(postId, userId);
        return Result.success(detail);
    }

    // ==================== 点赞相关接口 ====================

    /**
     * 点赞/取消点赞
     */
    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/like/{postId}")
    public Result<Void> toggleLike(@PathVariable Long postId) {
        Long userId = getCurrentUserId();
        circleService.toggleLike(postId, userId);
        return Result.success();
    }

    /**
     * 检查是否已点赞
     */
    @Operation(summary = "检查是否已点赞")
    @GetMapping("/like/check/{postId}")
    public Result<Boolean> checkLikeStatus(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        Boolean isLiked = circleService.checkLikeStatus(postId, userId);
        return Result.success(isLiked);
    }

    // ==================== 评论相关接口 ====================

    /**
     * 获取动态评论列表
     */
    @Operation(summary = "获取动态评论列表")
    @GetMapping("/comment/{postId}")
    public Result<List<CircleCommentVO>> getComments(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        List<CircleCommentVO> comments = circleService.getComments(postId, userId);
        return Result.success(comments);
    }

    /**
     * 发表评论
     */
    @Operation(summary = "发表评论")
    @PostMapping("/comment")
    public Result<Long> createComment(@Valid @RequestBody CircleCommentRequest request) {
        Long userId = getCurrentUserId();

        // 参数校验
        if (request.getPostId() == null) {
            throw new BusinessException(400, "动态ID不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "评论内容不能为空");
        }

        Long commentId = circleService.createComment(
                request.getPostId(),
                request.getContent(),
                request.getParentId(),
                request.getReplyToUserId(),
                userId
        );
        return Result.success(commentId);
    }

    /**
     * 删除评论
     */
    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = getCurrentUserId();
        circleService.deleteComment(commentId, userId);
        return Result.success();
    }

    // ==================== 转发相关接口 ====================

    /**
     * 转发动态
     */
    @Operation(summary = "转发动态")
    @PostMapping("/repost/{postId}")
    public Result<Long> repostPost(
            @PathVariable Long postId,
            @RequestParam(required = false) String content) {
        Long userId = getCurrentUserId();
        Long newPostId = circleService.repostPost(postId, content, userId);
        return Result.success(newPostId);
    }

    // ==================== 搜索相关接口 ====================

    /**
     * 搜索动态
     */
    @Operation(summary = "搜索动态")
    @GetMapping("/search")
    public Result<List<CirclePostVO>> searchPosts(
            @RequestParam @NotBlank String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        List<CirclePostVO> results = circleService.searchPosts(keyword, page, pageSize, userId);
        return Result.success(results);
    }

    // ==================== 私有方法 ====================

    private Long getCurrentUserId() {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}