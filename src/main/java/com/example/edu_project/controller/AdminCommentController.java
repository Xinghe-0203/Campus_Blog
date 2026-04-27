package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.BlogCommentService;
import com.example.edu_project.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员评论管理控制器
 */
@Tag(name = "管理员-评论管理", description = "管理员评论管理接口")
@RestController
@RequestMapping("/admin/comment")
@Validated
public class AdminCommentController {

    private static final Logger log = LoggerFactory.getLogger(AdminCommentController.class);

    @Autowired
    private BlogCommentService blogCommentService;

    /**
     * 管理员删除评论（级联删除子评论）
     */
    @Operation(summary = "管理员删除评论")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> deleteComment(@PathVariable Long id) {
        // 获取管理员ID用于日志
        Long adminId = SecurityUtils.getCurrentUserId();

        // 执行删除（管理员删除不需要检查评论作者权限）
        blogCommentService.adminDeleteComment(id, adminId);

        return Result.success(null);
    }
}
