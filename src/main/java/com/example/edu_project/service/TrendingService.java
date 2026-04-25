package com.example.edu_project.service;

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
    Object getHotPosts(int pageNum, int pageSize);

    /**
     * 获取热门标签列表（公开接口）
     * @return 热门标签列表（按使用次数降序）
     */
    Object getHotTags();

    /**
     * 更新单篇文章的热度
     * @param postId 文章ID
     */
    void updatePostTrending(Long postId);

    /**
     * 定时任务：更新所有文章热度
     * 每天凌晨执行，无参数
     */
    void scheduledUpdateAllTrending();
}