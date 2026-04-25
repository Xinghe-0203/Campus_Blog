package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.entity.BlogCollect;
import com.example.edu_project.vo.CollectItemVO;
import com.example.edu_project.vo.CollectResultVO;
import com.example.edu_project.vo.CollectStatusVO;

/**
 * 收藏服务接口
 */
public interface BlogCollectService extends IService<BlogCollect> {

    /**
     * 收藏/取消收藏
     * @param postId 文章ID
     * @param userId 用户ID
     * @return 操作结果
     */
    CollectResultVO toggleCollect(Long postId, Long userId);

    /**
     * 检查是否已收藏
     * @param postId 文章ID
     * @param userId 用户ID
     * @return 收藏状态
     */
    CollectStatusVO checkCollectStatus(Long postId, Long userId);

    /**
     * 获取我的收藏列表（分页）
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    IPage<CollectItemVO> getMyCollections(Long userId, Integer page, Integer pageSize);
}
