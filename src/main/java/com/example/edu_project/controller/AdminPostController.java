package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.AdminPostQueryRequest;
import com.example.edu_project.dto.RejectPostRequest;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.PostDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员文章管理控制器
 */
@Slf4j
@Tag(name = "管理员-文章管理", description = "管理员文章管理接口")
@RestController
@RequestMapping("/admin/post")
@Validated
public class AdminPostController {

    @Autowired
    private BlogPostService blogPostService;

    /**
     * 获取文章列表（管理员）
     */
    @Operation(summary = "获取文章列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('admin')")
    public Result<IPage<PostDetailResponse>> getPostList(@Valid AdminPostQueryRequest request) {
        IPage<PostDetailResponse> result = blogPostService.getAdminPostList(request);
        return Result.success(result);
    }

    /**
     * 管理员删除文章（级联删除）
     */
    @Operation(summary = "管理员删除文章")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> deletePost(@PathVariable Long id) {
        // 获取管理员ID用于日志
        Long adminId = SecurityUtils.getCurrentUserId();

        // 执行删除（管理员删除不需要检查文章作者权限）
        blogPostService.adminDeletePost(id, adminId);

        return Result.success(null);
    }

    /**
     * 获取待审核文章列表
     */
    @Operation(summary = "获取待审核文章列表")
    @GetMapping("/review-list")
    @PreAuthorize("hasRole('admin')")
    public Result<IPage<PostDetailResponse>> getReviewList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        IPage<PostDetailResponse> result = blogPostService.getReviewList(keyword, page, pageSize);
        return Result.success(result);
    }

    /**
     * 审核通过文章
     */
    @Operation(summary = "审核通过文章")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> approvePost(@PathVariable Long id) {
        Long reviewerId = SecurityUtils.getCurrentUserId();
        blogPostService.approvePost(id, reviewerId);
        return Result.success(null);
    }

    /**
     * 驳回文章
     */
    @Operation(summary = "驳回文章")
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> rejectPost(@PathVariable Long id,
                                    @Valid @RequestBody RejectPostRequest request) {
        Long reviewerId = SecurityUtils.getCurrentUserId();
        blogPostService.rejectPost(id, reviewerId, request.getReason());
        return Result.success(null);
    }
}
