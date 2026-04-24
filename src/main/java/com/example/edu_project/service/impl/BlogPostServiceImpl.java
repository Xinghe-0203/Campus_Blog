package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.mapper.BlogPostMapper;
import com.example.edu_project.service.BlogPostService;
import org.springframework.stereotype.Service;

/**
 * 文章服务实现类
 */
@Service
public class BlogPostServiceImpl extends ServiceImpl<BlogPostMapper, BlogPost> implements BlogPostService {
}
