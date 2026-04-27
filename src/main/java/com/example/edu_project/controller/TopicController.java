package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.TopicCreateRequest;
import com.example.edu_project.entity.CirclePost;
import com.example.edu_project.entity.Topic;
import com.example.edu_project.service.CircleService;
import com.example.edu_project.service.TopicService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.CirclePostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 话题控制器
 */
@Slf4j
@Tag(name = "话题", description = "话题相关接口")
@RestController
@RequestMapping("/topic")
@Validated
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private CircleService circleService;

    /**
     * 创建话题（仅管理员）
     */
    @Operation(summary = "创建话题（仅管理员）")
    @PostMapping
    public Result<Long> createTopic(@Valid @RequestBody TopicCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Long topicId = topicService.createTopic(request.getName(), request.getDescription());
        return Result.success(topicId);
    }

    /**
     * 获取话题列表（分页）
     */
    @Operation(summary = "获取话题列表")
    @GetMapping("/list")
    public Result<List<Topic>> getTopicList(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) {
        return Result.success(topicService.getTopicList(page, pageSize).getRecords());
    }

    /**
     * 获取热门话题
     */
    @Operation(summary = "获取热门话题")
    @GetMapping("/hot")
    public Result<List<Topic>> getHotTopics(
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        return Result.success(topicService.getHotTopics(limit));
    }

    /**
     * 获取话题详情
     */
    @Operation(summary = "获取话题详情")
    @GetMapping("/{topicId}")
    public Result<Topic> getTopicById(@PathVariable Long topicId) {
        return Result.success(topicService.getTopicById(topicId));
    }

    /**
     * 获取话题下的动态列表
     */
    @Operation(summary = "获取话题下的动态列表")
    @GetMapping("/{topicId}/posts")
    public Result<List<CirclePostVO>> getTopicPosts(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        List<CirclePostVO> posts = circleService.getPostsByTopic(topicId, page, pageSize, userId);
        return Result.success(posts);
    }
}