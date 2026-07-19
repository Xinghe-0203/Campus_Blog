package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.TrendingService;
import com.example.edu_project.vo.HotPostVO;
import com.example.edu_project.vo.HotTagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 趋势/热门内容控制器
 */
@Tag(name = "趋势/热门", description = "热门文章和标签相关接口")
@RestController
@RequestMapping("/trending")
@Validated
public class TrendingController {

    @Autowired
    private TrendingService trendingService;

    /**
     * 获取热门文章列表（公开，支持分页）
     */
    @Operation(summary = "获取热门文章列表")
    @GetMapping("/posts")
    public Result<IPage<HotPostVO>> getHotPosts(
            @RequestParam(defaultValue = "1") @Min(1) int pageNum,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        IPage<HotPostVO> result = trendingService.getHotPosts(pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取热门标签（公开）
     */
    @Operation(summary = "获取热门标签")
    @GetMapping("/hot-tags")
    public Result<IPage<HotTagVO>> getHotTags() {
        IPage<HotTagVO> result = trendingService.getHotTags();
        return Result.success(result);
    }

    /**
     * 更新单篇文章热度（手动触发，仅管理员可用）
     */
    @Operation(summary = "更新文章热度")
    @PostMapping("/update/{postId}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> updatePostTrending(@PathVariable Long postId) {
        trendingService.updatePostTrending(postId);
        return Result.success(null);
    }
}