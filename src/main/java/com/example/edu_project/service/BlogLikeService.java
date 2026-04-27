package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.entity.BlogLike;
import com.example.edu_project.vo.LikeItemVO;
import com.example.edu_project.vo.LikeResultVO;
import com.example.edu_project.vo.LikeStatusVO;

/**
 * 点赞服务接口
 */
public interface BlogLikeService extends IService<BlogLike> {

    /**
     * 点赞/取消点赞
     * @param postId 文章ID
     * @param userId 用户ID
     * @return 操作结果
     */
    LikeResultVO toggleLike(Long postId, Long userId);

    /**
     * 检查是否已点赞
     * @param postId 文章ID
     * @param userId 用户ID
     * @return 点赞状态
     */
    LikeStatusVO checkLikeStatus(Long postId, Long userId);

    /**
     * 检查用户是否已点赞某文章
     */
    boolean hasLiked(Long postId, Long userId);

    /**
     * 获取我的点赞列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    IPage<LikeItemVO> getMyLikes(Long userId, Integer page, Integer pageSize);
}
