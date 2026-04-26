package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.FollowRequest;
import com.example.edu_project.service.FollowService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.FollowStatusVO;
import com.example.edu_project.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关注控制器
 */
@Tag(name = "关注管理", description = "关注相关接口")
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 关注用户
     */
    @Operation(summary = "关注用户")
    @PostMapping
    public Result<FollowStatusVO> follow(@Valid @RequestBody FollowRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (request == null || request.getTargetUserId() == null) {
            throw new BusinessException(400, "目标用户ID不能为空");
        }
        FollowStatusVO result = followService.follow(request.getTargetUserId(), userId);
        return Result.success(result);
    }

    /**
     * 取消关注
     */
    @Operation(summary = "取消关注")
    @DeleteMapping("/{targetUserId}")
    public Result<FollowStatusVO> unfollow(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        FollowStatusVO result = followService.unfollow(targetUserId, userId);
        return Result.success(result);
    }

    /**
     * 检查是否关注
     */
    @Operation(summary = "检查是否关注")
    @GetMapping("/check/{targetUserId}")
    public Result<FollowStatusVO> checkFollow(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        boolean following = followService.isFollowing(targetUserId, userId);

        FollowStatusVO result = new FollowStatusVO();
        result.setFollowing(following);

        // 如果已登录，返回当前用户的关注数
        if (userId != null) {
            FollowService.FollowCountsVO counts = followService.getCounts(userId);
            result.setFollowingCount(counts.getFollowingCount());
        }

        return Result.success(result);
    }

    /**
     * 获取粉丝列表
     */
    @Operation(summary = "获取粉丝列表")
    @GetMapping("/followers/{userId}")
    public Result<List<UserVO>> getFollowers(@PathVariable Long userId) {
        List<UserVO> followers = followService.getFollowers(userId);
        return Result.success(followers);
    }

    /**
     * 获取关注列表
     */
    @Operation(summary = "获取关注列表")
    @GetMapping("/following/{userId}")
    public Result<List<UserVO>> getFollowing(@PathVariable Long userId) {
        List<UserVO> following = followService.getFollowing(userId);
        return Result.success(following);
    }

    /**
     * 获取粉丝/关注数量
     */
    @Operation(summary = "获取粉丝/关注数量")
    @GetMapping("/counts/{userId}")
    public Result<FollowService.FollowCountsVO> getCounts(@PathVariable Long userId) {
        FollowService.FollowCountsVO counts = followService.getCounts(userId);
        return Result.success(counts);
    }
}