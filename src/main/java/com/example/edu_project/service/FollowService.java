package com.example.edu_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.entity.BlogFollow;
import com.example.edu_project.vo.FollowStatusVO;
import com.example.edu_project.vo.UserVO;

import java.util.List;

/**
 * 关注服务接口
 */
public interface FollowService extends IService<BlogFollow> {

    /**
     * 关注用户
     * @param targetUserId 目标用户ID
     * @param currentUserId 当前用户ID
     * @return 关注状态
     */
    FollowStatusVO follow(Long targetUserId, Long currentUserId);

    /**
     * 取消关注
     * @param targetUserId 目标用户ID
     * @param currentUserId 当前用户ID
     * @return 关注状态
     */
    FollowStatusVO unfollow(Long targetUserId, Long currentUserId);

    /**
     * 检查是否关注
     * @param targetUserId 目标用户ID
     * @param currentUserId 当前用户ID
     * @return 是否关注
     */
    boolean isFollowing(Long targetUserId, Long currentUserId);

    /**
     * 获取粉丝列表
     * @param userId 用户ID
     * @return 粉丝列表
     */
    List<UserVO> getFollowers(Long userId);

    /**
     * 获取关注列表
     * @param userId 用户ID
     * @return 关注列表
     */
    List<UserVO> getFollowing(Long userId);

    /**
     * 获取用户粉丝数和关注数
     * @param userId 用户ID
     * @return 粉丝数和关注数
     */
    FollowCountsVO getCounts(Long userId);

    /**
     * 粉丝数和关注数 VO
     */
    class FollowCountsVO {
        private Long userId;
        private Integer followerCount;
        private Integer followingCount;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getFollowerCount() { return followerCount; }
        public void setFollowerCount(Integer followerCount) { this.followerCount = followerCount; }
        public Integer getFollowingCount() { return followingCount; }
        public void setFollowingCount(Integer followingCount) { this.followingCount = followingCount; }
    }
}