package com.example.edu_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.entity.BlogPostTag;

import java.util.List;

/**
 * 文章-标签关联服务接口
 */
public interface BlogPostTagService extends IService<BlogPostTag> {

    /**
     * 批量插入文章标签关联
     * @param postId 文章ID
     * @param tagIds 标签ID列表
     */
    void batchInsertPostTags(Long postId, List<Long> tagIds);
}
