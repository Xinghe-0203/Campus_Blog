package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.vo.HotPostVO;
import com.example.edu_project.vo.HotTagVO;

/**
 * 趋势/热门内容服务接口
 */
public interface TrendingService {

    /**
     * 获取热门文章列表（公开接口，支持分页）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 热门文章列表（按热度评分降序）
     */
    IPage<HotPostVO> getHotPosts(int pageNum, int pageSize);

    /**
     * 获取热门标签列表（公开接口）
     * @return 热门标签列表（按使用次数降序）
     */
    IPage<HotTagVO> getHotTags();

    /**
     * 更新单篇文章的热度
     * @param postId 文章ID
     */
    void updatePostTrending(Long postId);

    void scheduledUpdateAllTrending();
}