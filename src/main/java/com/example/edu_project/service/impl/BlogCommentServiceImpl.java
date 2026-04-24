package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.entity.BlogComment;
import com.example.edu_project.mapper.BlogCommentMapper;
import com.example.edu_project.service.BlogCommentService;
import org.springframework.stereotype.Service;

/**
 * 评论服务实现类
 */
@Service
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements BlogCommentService {
}
