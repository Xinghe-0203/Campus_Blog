package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.entity.BlogCollect;
import com.example.edu_project.mapper.BlogCollectMapper;
import com.example.edu_project.service.BlogCollectService;
import org.springframework.stereotype.Service;

/**
 * 收藏服务实现类
 */
@Service
public class BlogCollectServiceImpl extends ServiceImpl<BlogCollectMapper, BlogCollect> implements BlogCollectService {
}
