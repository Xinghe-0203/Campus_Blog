package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.entity.BlogPostTag;
import com.example.edu_project.mapper.BlogPostTagMapper;
import com.example.edu_project.service.BlogPostTagService;
import org.springframework.stereotype.Service;

/**
 * 文章-标签关联服务实现类
 */
@Service
public class BlogPostTagServiceImpl extends ServiceImpl<BlogPostTagMapper, BlogPostTag> implements BlogPostTagService {
}
