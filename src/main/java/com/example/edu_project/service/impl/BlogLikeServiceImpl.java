package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.entity.BlogLike;
import com.example.edu_project.mapper.BlogLikeMapper;
import com.example.edu_project.service.BlogLikeService;
import org.springframework.stereotype.Service;

/**
 * 点赞服务实现类
 */
@Service
public class BlogLikeServiceImpl extends ServiceImpl<BlogLikeMapper, BlogLike> implements BlogLikeService {
}
