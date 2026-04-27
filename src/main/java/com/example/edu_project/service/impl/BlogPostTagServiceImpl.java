package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.entity.BlogPostTag;
import com.example.edu_project.mapper.BlogPostTagMapper;
import com.example.edu_project.service.BlogPostTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文章-标签关联服务实现类
 */
@Service
public class BlogPostTagServiceImpl extends ServiceImpl<BlogPostTagMapper, BlogPostTag> implements BlogPostTagService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertPostTags(Long postId, java.util.List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        baseMapper.batchInsertPostTags(postId, tagIds);
    }
}
