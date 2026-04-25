package com.example.edu_project.controller;

import com.example.edu_project.common.result.Result;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.service.BlogTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}