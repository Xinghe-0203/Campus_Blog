package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.mapper.BlogTagMapper;
import com.example.edu_project.service.BlogTagService;
import org.springframework.stereotype.Service;

/**
 * 标签服务实现类
 */
@Service
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService {
}
