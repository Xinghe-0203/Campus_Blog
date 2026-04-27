package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.AdminPostQueryRequest;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.PostDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员文章管理控制器
 */
@Tag(name = "管理员-文章管理", description = "管理员文章管理接口")
@RestController
@RequestMapping("/admin/post")
@Validated
public class AdminPostController {

    private static final Logger log = LoggerFactory.getLogger(AdminPostController.class);

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
}
