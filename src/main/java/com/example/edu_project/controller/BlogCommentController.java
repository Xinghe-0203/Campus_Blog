package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.CommentCreateRequest;
import com.example.edu_project.service.BlogCommentService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.CommentVO;
import com.example.edu_project.vo.CommentWithPostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@Tag(name = "评论管理", description = "评论相关接口")
@RestController
@RequestMapping("/comment")
@Validated
public class BlogCommentController {

    @Autowired
    private BlogCommentService blogCommentService;

    /**
     * 发表评论
     */
    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Long> createComment(@Valid @RequestBody CommentCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Long commentId = blogCommentService.createComment(request, userId);
        return Result.success(commentId);
    }

    /**
     * 获取文章评论列表
     */
    @Operation(summary = "获取文章评论列表")
    @GetMapping("/post/{postId}")
    public Result<List<CommentVO>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentVO> comments = blogCommentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    /**
     * 删除评论
     */
    @Operation(summary = "删除评论")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        blogCommentService.deleteComment(commentId, userId);
        return Result.success(null);
    }

    /**
     * 获取我的评论列表
     */
    @Operation(summary = "获取我的评论列表")
    @GetMapping("/my")
    public Result<IPage<CommentWithPostVO>> getMyComments(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        IPage<CommentWithPostVO> result = blogCommentService.getMyComments(userId, page, pageSize);
        return Result.success(result);
    }
}
