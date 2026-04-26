package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.TagCreateRequest;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.service.BlogTagService;
import com.example.edu_project.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理控制器
 */
@Tag(name = "标签管理", description = "标签相关接口")
@RestController
@RequestMapping("/tag")
public class BlogTagController {

    @Autowired
    private BlogTagService blogTagService;

    /**
     * 获取所有标签列表
     */
    @Operation(summary = "获取所有标签")
    @GetMapping("/list")
    public Result<List<BlogTag>> listAllTags() {
        List<BlogTag> tags = blogTagService.listAllTags();
        return Result.success(tags);
    }

    /**
     * 创建标签
     */
    @Operation(summary = "创建标签")
    @PostMapping
    public Result<BlogTag> createTag(@Valid @RequestBody TagCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        BlogTag tag = blogTagService.createTag(request.getName());
        return Result.success(tag);
    }
}